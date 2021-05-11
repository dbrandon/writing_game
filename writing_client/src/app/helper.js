
export function setupMultilevelDropdown() {
  $('.dropdown-submenu > a').on('click', function (e) {
    var submenu = $(this);
    console.log('doin it')
    $('.dropdown-submenu .dropdown-menu').removeClass('show');
    submenu.next('.dropdown-menu').addClass('show');
    e.stopPropagation();
  })

  $('.dropright-submenu > a').on('click', function (e) {
    var submenu = $(this);
    console.log('doin it')
    $('.dropright-submenu .dropdown-menu').removeClass('show');
    submenu.next('.dropdown-menu').addClass('show');
    e.stopPropagation();
  })

  $('.dropdown').on('hidden.bs.dropdown', function() {
    $('.dropdown-menu.show').removeClass('show');
  })
}