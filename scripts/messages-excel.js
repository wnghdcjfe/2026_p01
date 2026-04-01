#!/usr/bin/env node
'use strict';

const fs = require('fs');
const path = require('path');

const ROOT = path.resolve(__dirname, '..');
const DEFAULT_KO = path.join(ROOT, 'src', 'main', 'resources', 'messages_ko.properties');
const DEFAULT_EN = path.join(ROOT, 'src', 'main', 'resources', 'messages_en.properties');
const DEFAULT_CSV = path.join(ROOT, 'i18n', 'messages.csv');

main(process.argv.slice(2));

function main(argv) {
  const parsed = parseArgs(argv);
  const command = parsed.command;

  if (!command || command === 'help' || parsed.options.help) {
    printHelp();
    return;
  }

  try {
    switch (command) {
      case 'export':
        exportToCsv({
          koPath: resolvePath(parsed.options.ko || DEFAULT_KO),
          enPath: resolvePath(parsed.options.en || DEFAULT_EN),
          csvPath: resolvePath(parsed.options.csv || DEFAULT_CSV),
        });
        return;
      case 'import':
      case 'sync':
        importFromCsv({
          csvPath: resolvePath(parsed.options.csv || DEFAULT_CSV),
          koPath: resolvePath(parsed.options.ko || DEFAULT_KO),
          enPath: resolvePath(parsed.options.en || DEFAULT_EN),
        });
        return;
      default:
        throw new Error(`Unknown command: ${command}`);
    }
  } catch (error) {
    console.error(`[messages-excel] ${error.message}`);
    process.exitCode = 1;
  }
}

function exportToCsv({ koPath, enPath, csvPath }) {
  assertFileExists(koPath);
  assertFileExists(enPath);

  const koEntries = readProperties(koPath);
  const enEntries = readProperties(enPath);
  const keys = [
    ...koEntries.order,
    ...enEntries.order.filter((key) => !Object.prototype.hasOwnProperty.call(koEntries.map, key)),
  ];

  const rows = [
    ['group', 'key', 'ko', 'en', 'status', 'notes'],
    ...keys.map((key) => {
      const group = key.includes('.') ? key.split('.')[0] : 'misc';
      const hasKo = Object.prototype.hasOwnProperty.call(koEntries.map, key);
      const hasEn = Object.prototype.hasOwnProperty.call(enEntries.map, key);
      const status = hasKo && hasEn ? 'ok' : hasKo ? 'missing_en' : hasEn ? 'missing_ko' : 'missing_both';
      return [
        group,
        key,
        hasKo ? koEntries.map[key] : '',
        hasEn ? enEntries.map[key] : '',
        status,
        '',
      ];
    }),
  ];

  ensureDir(path.dirname(csvPath));
  fs.writeFileSync(csvPath, writeCsv(rows), 'utf8');
  console.log(`[messages-excel] Exported ${keys.length} keys to ${relativeFromRoot(csvPath)}`);
}

function importFromCsv({ csvPath, koPath, enPath }) {
  assertFileExists(csvPath);

  const rows = readCsv(fs.readFileSync(csvPath, 'utf8'));
  if (rows.length === 0) {
    throw new Error(`CSV is empty: ${relativeFromRoot(csvPath)}`);
  }

  const header = rows[0].map((value) => value.trim().toLowerCase());
  const keyIndex = header.indexOf('key');
  const koIndex = header.indexOf('ko');
  const enIndex = header.indexOf('en');

  if (keyIndex === -1 || koIndex === -1 || enIndex === -1) {
    throw new Error('CSV header must include key, ko, en columns');
  }

  const koMap = Object.create(null);
  const enMap = Object.create(null);
  const order = [];
  const seen = new Set();

  for (let i = 1; i < rows.length; i += 1) {
    const row = rows[i];
    const key = (row[keyIndex] || '').trim();
    if (!key) {
      continue;
    }
    if (seen.has(key)) {
      throw new Error(`Duplicate key in CSV: ${key}`);
    }
    seen.add(key);
    order.push(key);
    koMap[key] = normalizeCell(row[koIndex]);
    enMap[key] = normalizeCell(row[enIndex]);
  }

  ensureDir(path.dirname(koPath));
  ensureDir(path.dirname(enPath));
  fs.writeFileSync(koPath, writeProperties(order, koMap), 'utf8');
  fs.writeFileSync(enPath, writeProperties(order, enMap), 'utf8');
  console.log(`[messages-excel] Imported ${order.length} keys from ${relativeFromRoot(csvPath)}`);
  console.log(`[messages-excel] Wrote ${relativeFromRoot(koPath)} and ${relativeFromRoot(enPath)}`);
}

function readProperties(filePath) {
  const text = fs.readFileSync(filePath, 'utf8').replace(/^\uFEFF/, '');
  const logicalLines = toLogicalLines(text);
  const map = Object.create(null);
  const order = [];

  for (const line of logicalLines) {
    const trimmedLeft = line.replace(/^\s+/, '');
    if (!trimmedLeft || trimmedLeft.startsWith('#') || trimmedLeft.startsWith('!')) {
      continue;
    }

    const { key, value } = splitPropertyLine(trimmedLeft);
    if (!key) {
      continue;
    }

    const decodedKey = unescapeProperty(key);
    const decodedValue = unescapeProperty(value);

    if (!Object.prototype.hasOwnProperty.call(map, decodedKey)) {
      order.push(decodedKey);
    }
    map[decodedKey] = decodedValue;
  }

  return { map, order };
}

function toLogicalLines(text) {
  const lines = text.split(/\r?\n/);
  const logical = [];
  let buffer = '';

  for (const rawLine of lines) {
    if (buffer === '') {
      buffer = rawLine;
    } else {
      buffer += rawLine.replace(/^\s+/, '');
    }

    if (hasContinuation(buffer)) {
      buffer = buffer.slice(0, -1);
      continue;
    }

    logical.push(buffer);
    buffer = '';
  }

  if (buffer !== '') {
    logical.push(buffer);
  }

  return logical;
}

function hasContinuation(line) {
  let backslashCount = 0;
  for (let i = line.length - 1; i >= 0 && line[i] === '\\'; i -= 1) {
    backslashCount += 1;
  }
  return backslashCount % 2 === 1;
}

function splitPropertyLine(line) {
  let escaped = false;
  let separatorIndex = -1;
  let separatorKind = '';

  for (let i = 0; i < line.length; i += 1) {
    const char = line[i];
    if (escaped) {
      escaped = false;
      continue;
    }
    if (char === '\\') {
      escaped = true;
      continue;
    }
    if (char === '=' || char === ':') {
      separatorIndex = i;
      separatorKind = char;
      break;
    }
    if (/\s/.test(char)) {
      separatorIndex = i;
      separatorKind = 'whitespace';
      break;
    }
  }

  if (separatorIndex === -1) {
    return { key: line, value: '' };
  }

  let valueStart = separatorIndex + 1;
  if (separatorKind === 'whitespace') {
    while (valueStart < line.length && /\s/.test(line[valueStart])) {
      valueStart += 1;
    }
    if (valueStart < line.length && (line[valueStart] === '=' || line[valueStart] === ':')) {
      valueStart += 1;
    }
  }
  while (valueStart < line.length && /\s/.test(line[valueStart])) {
    valueStart += 1;
  }

  return {
    key: line.slice(0, separatorIndex),
    value: line.slice(valueStart),
  };
}

function unescapeProperty(value) {
  let result = '';
  for (let i = 0; i < value.length; i += 1) {
    const char = value[i];
    if (char !== '\\') {
      result += char;
      continue;
    }

    i += 1;
    if (i >= value.length) {
      result += '\\';
      break;
    }

    const next = value[i];
    switch (next) {
      case 't':
        result += '\t';
        break;
      case 'r':
        result += '\r';
        break;
      case 'n':
        result += '\n';
        break;
      case 'f':
        result += '\f';
        break;
      case 'u': {
        const code = value.slice(i + 1, i + 5);
        if (/^[0-9a-fA-F]{4}$/.test(code)) {
          result += String.fromCharCode(parseInt(code, 16));
          i += 4;
        } else {
          result += 'u';
        }
        break;
      }
      default:
        result += next;
        break;
    }
  }
  return result;
}

function writeProperties(order, map) {
  const lines = order.map((key) => `${escapeProperty(key, true)}=${escapeProperty(map[key] || '', false)}`);
  return `${lines.join('\n')}\n`;
}

function escapeProperty(value, isKey) {
  let result = '';
  for (let i = 0; i < value.length; i += 1) {
    const char = value[i];
    if (char === '\\') {
      result += '\\\\';
      continue;
    }
    if (char === '\t') {
      result += '\\t';
      continue;
    }
    if (char === '\n') {
      result += '\\n';
      continue;
    }
    if (char === '\r') {
      result += '\\r';
      continue;
    }
    if (char === '\f') {
      result += '\\f';
      continue;
    }
    if (i === 0 && char === ' ') {
      result += '\\ ';
      continue;
    }
    if (isKey && (char === '=' || char === ':' || char === '#' || char === '!' || char === ' ')) {
      result += `\\${char}`;
      continue;
    }
    result += char;
  }
  return result;
}

function readCsv(text) {
  const input = text.replace(/^\uFEFF/, '');
  const rows = [];
  let row = [];
  let field = '';
  let inQuotes = false;

  for (let i = 0; i < input.length; i += 1) {
    const char = input[i];

    if (inQuotes) {
      if (char === '"') {
        if (input[i + 1] === '"') {
          field += '"';
          i += 1;
        } else {
          inQuotes = false;
        }
      } else {
        field += char;
      }
      continue;
    }

    if (char === '"') {
      inQuotes = true;
      continue;
    }
    if (char === ',') {
      row.push(field);
      field = '';
      continue;
    }
    if (char === '\n') {
      row.push(field);
      rows.push(row);
      row = [];
      field = '';
      continue;
    }
    if (char === '\r') {
      continue;
    }
    field += char;
  }

  if (field.length > 0 || row.length > 0) {
    row.push(field);
    rows.push(row);
  }

  return rows;
}

function writeCsv(rows) {
  const lines = rows.map((row) => row.map(escapeCsv).join(','));
  return `\uFEFF${lines.join('\r\n')}\r\n`;
}

function escapeCsv(value) {
  const text = value == null ? '' : String(value);
  if (/[,"\n\r]/.test(text) || /^\s|\s$/.test(text)) {
    return `"${text.replace(/"/g, '""')}"`;
  }
  return text;
}

function normalizeCell(value) {
  return value == null ? '' : String(value);
}

function parseArgs(argv) {
  const [command, ...rest] = argv;
  const options = {};

  for (let i = 0; i < rest.length; i += 1) {
    const token = rest[i];
    if (!token.startsWith('--')) {
      continue;
    }
    const eqIndex = token.indexOf('=');
    if (eqIndex !== -1) {
      options[token.slice(2, eqIndex)] = token.slice(eqIndex + 1);
      continue;
    }
    const key = token.slice(2);
    const next = rest[i + 1];
    if (!next || next.startsWith('--')) {
      options[key] = true;
      continue;
    }
    options[key] = next;
    i += 1;
  }

  return { command, options };
}

function resolvePath(targetPath) {
  return path.isAbsolute(targetPath) ? targetPath : path.resolve(ROOT, targetPath);
}

function relativeFromRoot(targetPath) {
  return path.relative(ROOT, targetPath) || '.';
}

function ensureDir(dirPath) {
  fs.mkdirSync(dirPath, { recursive: true });
}

function assertFileExists(filePath) {
  if (!fs.existsSync(filePath)) {
    throw new Error(`File not found: ${relativeFromRoot(filePath)}`);
  }
}

function printHelp() {
  console.log(`messages-excel.js\n\n` +
    `Excel-friendly CSV workflow for src/main/resources/messages_ko.properties and messages_en.properties.\n\n` +
    `Usage:\n` +
    `  node scripts/messages-excel.js export [--csv i18n/messages.csv] [--ko path] [--en path]\n` +
    `  node scripts/messages-excel.js import [--csv i18n/messages.csv] [--ko path] [--en path]\n\n` +
    `Notes:\n` +
    `  - The generated CSV is UTF-8 with BOM, so Excel opens Korean text correctly.\n` +
    `  - Edit the key/ko/en columns in Excel, save as CSV UTF-8, then run import.\n`);
}
