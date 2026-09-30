function pintarNav() {
  const cont = document.getElementById('nav-usuario');
  if (!cont) return;

  const u = Api.usuario();

  // 1. Dibuja estado del usuario manteniendo las clases de estilo
  if (!u) {
    cont.innerHTML = `<a href="/login" class="btn btn-primary btn-sm btn-ingresar">Ingresar</a>`;
  } else {
    cont.innerHTML = `
      <span class="usuario-info me-2"><strong>${escapeHtml(u.nombre)}</strong> (${escapeHtml(u.rol)})</span>
      <button id="btn-logout" class="btn btn-outline-danger btn-sm btn-salir">Salir</button>
    `;

    document.getElementById('btn-logout').addEventListener('click', () => {
      Api.cerrarSesion();
      window.location.href = '/';
    });
  }

  // 2. Control por ROL (Si no hay usuario o el rol no coincide -> oculta)
  document.querySelectorAll('[data-rol]').forEach(el => {
    if (!u || !u.rol) {
      el.classList.add('oculto');
      return;
    }
    const rolesPermitidos = el.dataset.rol.split(',').map(r => r.trim().toUpperCase());
    const rolUsuario = u.rol.toUpperCase();

    el.classList.toggle('oculto', !rolesPermitidos.includes(rolUsuario));
  });

  // 3. Control solo por AUTENTICACIÓN (Ignora elementos que tengan data-rol explícito)
  document.querySelectorAll('[data-autenticado]:not([data-rol])').forEach(el => {
    el.classList.toggle('oculto', !u);
  });
}

document.addEventListener('DOMContentLoaded', pintarNav);