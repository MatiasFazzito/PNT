document.getElementById('form-login').addEventListener('submit', async (e) => {
  e.preventDefault();
  const mensaje = document.getElementById('mensaje');
  mensaje.className = 'form-mensaje';
  const email = document.getElementById('email').value.trim();
  const password = document.getElementById('password').value;

  try {
    const resp = await Api.login(email, password);
    Api.guardarSesion(resp);
    window.location.href = 'index.html';
  } catch (err) {
    mensaje.textContent = err.message;
    mensaje.className = 'form-mensaje error';
  }
});