// Pinta el estado de sesión en la barra de navegación de cualquier página que la incluya.
function pintarNav() {
  const cont = document.getElementById('nav-usuario');
  if (!cont) return;

  const u = Api.usuario();
  if (!u) {
    cont.innerHTML = `<a href="login.html">Ingresar</a>`;
    return;
  }

  cont.innerHTML = `${u.nombre} (${u.rol}) &nbsp; <button class="btn-link" id="btn-logout">Salir</button>`;
  document.getElementById('btn-logout').addEventListener('click', () => {
    Api.cerrarSesion();
    window.location.href = 'index.html';
  });

  // Muestra/oculta enlaces según rol
  document.querySelectorAll('[data-rol]').forEach(el => {
    const roles = el.dataset.rol.split(',');
    el.classList.toggle('oculto', !roles.includes(u.rol));
  });
  document.querySelectorAll('[data-autenticado]').forEach(el => {
    el.classList.remove('oculto');
  });
}

function requiereRol(...roles) {
  if (!Api.tieneRol(...roles)) {
    window.location.href = 'login.html';
  }
}

function requiereLogin() {
  if (!Api.estaLogueado()) {
    window.location.href = 'login.html';
  }
}

document.addEventListener('DOMContentLoaded', pintarNav);
