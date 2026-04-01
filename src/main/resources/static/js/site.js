document.addEventListener('DOMContentLoaded', () => {
  const menuToggle = document.querySelector('[data-menu-toggle]');
  const menuPanel = document.querySelector('[data-menu-panel]');

  if (menuToggle && menuPanel) {
    menuToggle.addEventListener('click', () => {
      const open = menuPanel.classList.toggle('is-open');
      menuToggle.setAttribute('aria-expanded', String(open));
    });
  }

  document.querySelectorAll('[data-accordion-button]').forEach((button) => {
    button.addEventListener('click', () => {
      const expanded = button.getAttribute('aria-expanded') === 'true';
      const panel = button.nextElementSibling;
      button.setAttribute('aria-expanded', String(!expanded));
      if (panel) {
        panel.hidden = expanded;
      }
    });
  });

  const searchInput = document.querySelector('[data-faq-search]');
  if (searchInput) {
    const items = Array.from(document.querySelectorAll('.accordion-item[data-search]'));
    const categories = Array.from(document.querySelectorAll('[data-faq-category]'));
    const emptyState = document.querySelector('[data-faq-empty]');

    const applyFilter = () => {
      const query = searchInput.value.trim().toLowerCase();
      let visibleCount = 0;

      items.forEach((item) => {
        const haystack = item.dataset.search || '';
        const visible = !query || haystack.includes(query);
        item.hidden = !visible;
        if (visible) {
          visibleCount += 1;
        }
      });

      categories.forEach((category) => {
        const visibleItems = category.querySelectorAll('.accordion-item:not([hidden])').length;
        category.hidden = visibleItems === 0;
      });

      if (emptyState) {
        emptyState.hidden = visibleCount !== 0;
      }
    };

    searchInput.addEventListener('input', applyFilter);
    applyFilter();
  }
});
