document.addEventListener("DOMContentLoaded", () => {
    actualizarContadorCarrito();
    if (window.location.pathname.includes("/carrito")) {
        renderizarTablaCarrito();
    }
});

function obtenerCarrito() {
    return JSON.parse(localStorage.getItem("bf_carrito") || "[]");
}

function guardarCarrito(carrito) {
    localStorage.setItem("bf_carrito", JSON.stringify(carrito));
    actualizarContadorCarrito();
}

function agregarAlCarrito(id, nombre, precio, stock) {
    let carrito = obtenerCarrito();
    let item = carrito.find(x => x.id === id);

    if (item) {
        if (item.cantidad < stock) {
            item.cantidad++;
        } else {
            alert("No hay suficiente stock disponible.");
            return;
        }
    } else {
        carrito.push({ id: id, nombre: nombre, precio: precio, cantidad: 1, stock: stock });
    }

    guardarCarrito(carrito);
    alert(nombre + " fue agregado al carrito.");
}

function actualizarContadorCarrito() {
    let carrito = obtenerCarrito();
    let totalItems = carrito.reduce((sum, item) => sum + item.cantidad, 0);
    let badge = document.getElementById("cart-count");
    if (badge) badge.innerText = totalItems;
}

function renderizarTablaCarrito() {
    let carrito = obtenerCarrito();
    let tbody = document.getElementById("tabla-carrito");
    let totalSpan = document.getElementById("total-carrito");
    
    if (!tbody) return;

    tbody.innerHTML = "";
    let totalGeneral = 0;

    carrito.forEach((item, index) => {
        let subtotal = item.precio * item.cantidad;
        totalGeneral += subtotal;

        let row = `<tr>
            <td>${item.nombre}</td>
            <td>$${item.precio}</td>
            <td>${item.cantidad}</td>
            <td>$${subtotal}</td>
            <td><button class="btn" onclick="eliminarDelCarrito(${index})">Eliminar</button></td>
        </tr>`;
        tbody.innerHTML += row;
    });

    if (totalSpan) totalSpan.innerText = totalGeneral.toFixed(2);
}

function eliminarDelCarrito(index) {
    let carrito = obtenerCarrito();
    carrito.splice(index, 1);
    guardarCarrito(carrito);
    renderizarTablaCarrito();
}

function finalizarPedido() {
    let carrito = obtenerCarrito();
    if (carrito.length === 0) {
        alert("El carrito está vacío.");
        return;
    }
    alert("¡Pedido realizado con éxito!");
    localStorage.removeItem("bf_carrito");
    window.location.href = "/cocina";
}

function validarRegistro() {
    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;

    const emailRegex = /^[\s@]+@[\s@]+\.[\s@]+$/;
    if (!emailRegex.test(email)) {
        alert("Por favor, ingrese un email válido (ejemplo@dominio.com).");
        return false;
    }

    if (password.length < 8 || !/\d/.test(password) || !/[a-zA-Z]/.test(password)) {
        alert("La contraseña debe tener al menos 8 caracteres e incluir letras y números.");
        return false;
    }

    return true;
}