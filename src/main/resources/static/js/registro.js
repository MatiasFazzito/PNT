document.addEventListener('DOMContentLoaded', () => {
  const form = document.getElementById('form-registro');
  if (!form) return;

  form.addEventListener('submit', async (e) => {
    e.preventDefault();
    const mensaje = document.getElementById('mensaje');
    
    if (mensaje) {
      mensaje.textContent = '';
      mensaje.className = 'form-mensaje';
    }

    const datos = {
      nombre: document.getElementById('nombre').value.trim(),
      email: document.getElementById('email').value.trim(),
      telefono: document.getElementById('telefono').value.trim(),
      password: document.getElementById('password').value
    };

    try {
      const resp = await Api.registrar(datos);
      Api.guardarSesion(resp);
      window.location.href = '/'; // Redirección estandarizada
    } catch (err) {
      if (mensaje) {
        mensaje.textContent = err.message;
        mensaje.className = 'form-mensaje error';
      } else {
        alert(err.message);
      }
    }
  });
});