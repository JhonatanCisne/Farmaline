const API_BASE_URL = 'http://localhost:8080/api';
const USER_ID_STORAGE_KEY = 'farmalineUserId'; 

let userCartId = null; 
let cartItems = []; 

function showNotification(message, type = 'success') {
    const alertContainer = document.createElement('div');
    alertContainer.className = `alert alert-${type} alert-dismissible fade show fixed-top-alert`;
    alertContainer.setAttribute('role', 'alert');
    alertContainer.innerHTML = `
        ${message}
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    `;
    document.body.appendChild(alertContainer);

    setTimeout(() => {
        const bsAlert = new bootstrap.Alert(alertContainer);
        if (bsAlert) { 
            bsAlert.dispose(); 
        }
        alertContainer.remove();
    }, 5000);
}

function getUserId() {
    const userId = localStorage.getItem(USER_ID_STORAGE_KEY);
    if (!userId) {
        showNotification('No se pudo obtener el ID del usuario. Por favor, inicie sesión.', 'danger');
        return null;
    }
    return parseInt(userId);
}

async function fetchUserCartId(userId) {
    try {
        const response = await fetch(`${API_BASE_URL}/carritos/usuario/${userId}`);
        if (response.ok) {
            const cart = await response.json();
            return cart.idCarrito; 
        } else if (response.status === 404) {
            console.log('Carrito no encontrado para el usuario, creando uno nuevo.');
            const createResponse = await fetch(`${API_BASE_URL}/carritos`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ idUsuario: userId }) 
            });
            if (createResponse.ok) {
                const newCart = await createResponse.json();
                showNotification('Se ha creado un nuevo carrito para tu usuario.', 'info');
                return newCart.idCarrito; 
            } else {
                const errorText = await createResponse.text();
                console.error('Error al crear el carrito:', createResponse.status, errorText);
                showNotification(`Error al crear el carrito: ${errorText}`, 'danger');
                return null;
            }
        } else {
            const errorText = await response.text();
            console.error('Error al obtener el carrito:', response.status, errorText);
            showNotification(`Error al obtener el carrito: ${errorText}`, 'danger');
            return null;
        }
    } catch (error) {
        console.error('Error de red al obtener/crear carrito:', error);
        showNotification('Error de red al conectar con el servidor para el carrito.', 'danger');
        return null;
    }
}

async function fetchCartItems(cartId) {
    try {
        const response = await fetch(`${API_BASE_URL}/carrito-anadido/carrito/${cartId}`);
        if (response.ok) {
            return await response.json();
        } else if (response.status === 204) { 
            return [];
        } else {
            const errorText = await response.text();
            console.error('Error al obtener los ítems del carrito:', response.status, errorText);
            showNotification(`Error al cargar los productos de tu carrito: ${errorText}`, 'danger');
            return [];
        }
    } catch (error) {
        console.error('Error de red al obtener los ítems del carrito:', error);
        showNotification('Error de red al conectar con el servidor para los ítems del carrito.', 'danger');
        return [];
    }
}

async function fetchProductDetails(productId) {
    try {
        const response = await fetch(`${API_BASE_URL}/productos/${productId}`);
        if (response.ok) {
            return await response.json(); 
        } else {
            const errorText = await response.text();
            console.error(`Error al obtener detalles del producto ${productId}:`, response.status, errorText);
            return null;
        }
    } catch (error) {
        console.error(`Error de red al obtener detalles del producto ${productId}:`, error);
        return null;
    }
}

async function loadCartAndRender() {
    const userId = getUserId();
    if (!userId) {
        document.getElementById('emptyCartMessage').style.display = 'block'; 
        document.getElementById('clearCartBtn').style.display = 'none';
        document.getElementById('proceedToCheckoutBtn').disabled = true;
        updateCartTotals();
        return;
    }

    userCartId = await fetchUserCartId(userId);
    if (!userCartId) {
        document.getElementById('emptyCartMessage').style.display = 'block';
        document.getElementById('clearCartBtn').style.display = 'none';
        document.getElementById('proceedToCheckoutBtn').disabled = true;
        updateCartTotals();
        return;
    }

    const itemsInCartDTO = await fetchCartItems(userCartId);
    cartItems = [];

    if (itemsInCartDTO.length === 0) {
        document.getElementById('emptyCartMessage').style.display = 'block';
        document.getElementById('clearCartBtn').style.display = 'none';
        document.getElementById('proceedToCheckoutBtn').disabled = true;
    } else {
        document.getElementById('emptyCartMessage').style.display = 'none';
        document.getElementById('clearCartBtn').style.display = 'block'; 

        const productDetailsPromises = itemsInCartDTO.map(item => fetchProductDetails(item.idProducto));
        const products = await Promise.all(productDetailsPromises);

        itemsInCartDTO.forEach((itemDTO, index) => {
            const product = products[index];
            if (product) {
                cartItems.push({
                    id: itemDTO.idCarritoAnadido,
                    idProducto: itemDTO.idProducto,
                    name: product.nombre,
                    image: product.imagen,
                    price: product.precio,
                    quantity: itemDTO.cantidad
                });
            }
        });
    }

    renderCartItems();
    updateCartTotals();
}

function renderCartItems() {
    const container = document.getElementById('cartItemsContainer');
    if (cartItems.length > 0) {
        document.getElementById('emptyCartMessage').style.display = 'none';
        container.innerHTML = '';
    } else {
        document.getElementById('emptyCartMessage').style.display = 'block';
        document.getElementById('clearCartBtn').style.display = 'none';
        container.innerHTML = '';
    }

    cartItems.forEach(item => {
        const itemHtml = `
            <div class="card mb-3 cart-item" data-id="${item.id}" data-product-id="${item.idProducto}">
                <div class="row g-0">
                    <div class="col-md-3 d-flex justify-content-center align-items-center p-2">
                        <img src="${item.image}" class="img-fluid rounded-start cart-item-img" alt="${item.name}">
                    </div>
                    <div class="col-md-9">
                        <div class="card-body">
                            <h5 class="card-title">${item.name}</h5>
                            <p class="card-text text-muted">Precio unitario: S/. ${item.price.toFixed(2)}</p>
                            <div class="d-flex align-items-center mb-3">
                                <label for="quantity-${item.id}" class="form-label mb-0 me-2">Cantidad:</label>
                                <input type="number" id="quantity-${item.id}" class="form-control form-control-sm quantity-input" value="${item.quantity}" min="1" style="width: 70px;">
                                <span class="ms-3 fw-bold">Subtotal: S/. ${(item.price * item.quantity).toFixed(2)}</span>
                            </div>
                            <button class="btn btn-outline-danger btn-sm remove-item-btn" data-id="${item.id}">
                                <i class="fas fa-trash-alt me-1"></i> Eliminar
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        `;
        container.insertAdjacentHTML('beforeend', itemHtml);
    });

    addCartItemEventListeners();
}

function addCartItemEventListeners() {
    document.querySelectorAll('.remove-item-btn').forEach(button => {
        button.onclick = (event) => {
            const itemId = parseInt(event.target.closest('.remove-item-btn').dataset.id);
            if (confirm('¿Estás seguro de que quieres eliminar este producto del carrito?')) {
                removeItemFromCart(itemId);
            }
        };
    });

    document.querySelectorAll('.quantity-input').forEach(input => {
        input.onchange = (event) => {
            const itemId = parseInt(event.target.closest('.cart-item').dataset.id);
            const newQuantity = parseInt(event.target.value);
            if (newQuantity < 1) {
                if (confirm('¿Quieres eliminar este producto del carrito?')) {
                    removeItemFromCart(itemId);
                } else {
                    event.target.value = 1;
                    updateItemQuantity(itemId, 1); 
                }
            } else {
                updateItemQuantity(itemId, newQuantity);
            }
        };
    });

    document.getElementById('clearCartBtn').onclick = () => {
        if (confirm('¿Estás seguro de que quieres vaciar todo tu carrito?')) {
            clearEntireCart();
        }
    };
}

function updateCartTotals() {
    let subtotal = 0;
    cartItems.forEach(item => {
        subtotal += item.price * item.quantity;
    });

    const shipping = 0; 
    const discounts = 0; 

    const total = subtotal + shipping - discounts;

    document.getElementById('cartSubtotal').textContent = `S/. ${subtotal.toFixed(2)}`;
    document.getElementById('cartDiscounts').textContent = `S/. ${discounts.toFixed(2)}`;
    document.getElementById('cartTotal').textContent = `S/. ${total.toFixed(2)}`;

    document.getElementById('proceedToCheckoutBtn').disabled = cartItems.length === 0;
}

async function updateItemQuantity(itemId, newQuantity) {
    try {
        const response = await fetch(`${API_BASE_URL}/carrito-anadido/${itemId}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ cantidad: newQuantity }) 
        });

        if (response.ok) {
            showNotification('Cantidad del producto actualizada en el carrito.', 'success');
            await loadCartAndRender();
        } else {
            const errorText = await response.text();
            console.error('Error al actualizar cantidad:', response.status, errorText);
            showNotification(`Error al actualizar cantidad: ${errorText}`, 'danger');
            await loadCartAndRender(); 
        }
    } catch (error) {
        console.error('Error de red al actualizar cantidad:', error);
        showNotification('Error de red al actualizar la cantidad del producto.', 'danger');
    }
}

async function removeItemFromCart(itemId) {
    try {
        const response = await fetch(`${API_BASE_URL}/carrito-anadido/${itemId}`, {
            method: 'DELETE'
        });

        if (response.ok) {
            showNotification('Producto eliminado del carrito.', 'success');
            await loadCartAndRender();
        } else {
            const errorText = await response.text();
            console.error('Error al eliminar producto:', response.status, errorText);
            showNotification(`Error al eliminar producto: ${errorText}`, 'danger');
        }
    } catch (error) {
        console.error('Error de red al eliminar producto:', error);
        showNotification('Error de red al eliminar el producto del carrito.', 'danger');
    }
}

async function clearEntireCart() {
    if (!userCartId) {
        showNotification('No se pudo determinar el carrito a vaciar.', 'warning');
        return;
    }
    try {
        const response = await fetch(`${API_BASE_URL}/carrito-anadido/clear/${userCartId}`, {
            method: 'DELETE'
        });

        if (response.ok) {
            showNotification('Tu carrito ha sido vaciado.', 'success');
            await loadCartAndRender(); 
        } else {
            const errorText = await response.text();
            console.error('Error al vaciar el carrito:', response.status, errorText);
            showNotification(`Error al vaciar el carrito: ${errorText}`, 'danger');
        }
    } catch (error) {
        console.error('Error de red al vaciar el carrito:', error);
        showNotification('Error de red al vaciar el carrito.', 'danger');
    }
}

async function handleProceedToCheckout() {
    const userId = getUserId();
    if (!userId || !userCartId) {
        showNotification('No se pudo procesar el pedido. ID de usuario o carrito no disponible.', 'danger');
        return;
    }

    if (cartItems.length === 0) {
        showNotification('Tu carrito está vacío. Agrega productos antes de proceder al pago.', 'warning');
        return;
    }

    const now = new Date();
    const fecha = now.toISOString().split('T')[0];
    const hora = now.toLocaleTimeString('en-US', { hour12: false, hour: '2-digit', minute: '2-digit', second: '2-digit' });

    const pedidoDTO = {
        idUsuario: userId,
        idCarrito: userCartId,
        fecha: fecha,
        hora: hora,
    };

    try {
        const response = await fetch(`${API_BASE_URL}/pedidos`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(pedidoDTO)
        });

        if (response.status === 201) {
            const createdPedido = await response.json();
            showNotification('¡Pedido realizado con éxito!', 'success');
            console.log('Pedido creado:', createdPedido);

            await clearEntireCart(); 

            setTimeout(() => {
                window.location.href = 'Pedidos.html'; 
            }, 1500);
            
        } else {
            const errorText = await response.text();
            console.error('Error al crear el pedido:', response.status, errorText);
            showNotification(`Error al procesar el pedido: ${errorText}`, 'danger');
        }
    } catch (error) {
        console.error('Error de red al crear el pedido:', error);
        showNotification('Error de red al intentar conectar con el servidor para crear el pedido.', 'danger');
    }
}

document.addEventListener('DOMContentLoaded', () => {
    loadCartAndRender(); 

    const proceedToCheckoutBtn = document.getElementById('proceedToCheckoutBtn');
    if (proceedToCheckoutBtn) {
        proceedToCheckoutBtn.addEventListener('click', handleProceedToCheckout);
    }
});