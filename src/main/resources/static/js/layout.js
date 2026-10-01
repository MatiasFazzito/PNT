function requiereRol(rolRequerido) {
  const u = Api.usuario();
    if (!u || !u.rol || u.rol.toUpperCase() !== rolRequerido.toUpperCase()) {
    window.location.href = '/login';
  }
}

function pintarNav() {
  const cont = document.getElementById('nav-usuario');
  if (!cont) return;

  const u = Api.usuario();

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

  document.querySelectorAll('[data-rol]').forEach(el => {
    if (!u || !u.rol) {
      el.classList.add('oculto');
      return;
    }
    const rolesPermitidos = el.dataset.rol.split(',').map(r => r.trim().toUpperCase());
    const rolUsuario = u.rol.toUpperCase();

    el.classList.toggle('oculto', !rolesPermitidos.includes(rolUsuario));
  });

  document.querySelectorAll('[data-autenticado]:not([data-rol])').forEach(el => {
    el.classList.toggle('oculto', !u);
  });
}

document.addEventListener('DOMContentLoaded', pintarNav);