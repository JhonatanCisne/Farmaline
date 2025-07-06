const API_BASE_URL = 'http://localhost:8080/api';
const USER_ID_STORAGE_KEY = 'farmalineUserId';

let userCartId = null;
let cartItems = [];
const IGV_RATE = 0.18;

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
        const response = await fetch(`${API_BASE_URL}/carrito-anadidos/carrito/${cartId}`);
        if (response.ok) {
            const text = await response.text();
            return text ? JSON.parse(text) : [];
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

async function fetchAllProducts() {
    try {
        const response = await fetch(`${API_BASE_URL}/productos`);
        if (response.ok) {
            return await response.json();
        } else {
            const errorText = await response.text();
            console.error('Error al obtener todos los productos:', response.status, errorText);
            showNotification(`Error al cargar los productos disponibles: ${errorText}`, 'danger');
            return [];
        }
    } catch (error) {
        console.error('Error de red al obtener todos los productos:', error);
        showNotification('Error de red al conectar con el servidor para los productos.', 'danger');
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

async function renderProducts() {
    const productsContainer = document.getElementById('productsContainer');
    if (!productsContainer) return;

    allProducts = await fetchAllProducts();
    productsContainer.innerHTML = '';

    if (allProducts.length === 0) {
        productsContainer.innerHTML = '<p class="text-center text-muted">No hay productos disponibles en este momento.</p>';
        return;
    }

    allProducts.forEach(product => {
        const productCardHtml = `
            <div class="col-md-4 mb-4">
                <div class="card h-100 shadow-sm product-card">
                    <img src="${product.imagen}" class="card-img-top product-card-img" alt="${product.nombre}">
                    <div class="card-body d-flex flex-column">
                        <h5 class="card-title">${product.nombre}</h5>
                        <p class="card-text text-muted">${product.descripcion || 'Sin descripción.'}</p>
                        <p class="card-text fs-5 fw-bold mt-auto">S/. ${product.precio.toFixed(2)}</p>
                        <button class="btn btn-primary add-to-cart-btn mt-2" data-product-id="${product.idProducto}"
                                data-product-name="${product.nombre}" data-product-price="${product.precio}"
                                data-product-image="${product.imagen}">
                            <i class="fas fa-cart-plus me-2"></i>Añadir al Carrito
                        </button>
                    </div>
                </div>
            </div>
        `;
        productsContainer.insertAdjacentHTML('beforeend', productCardHtml);
    });

    document.querySelectorAll('.add-to-cart-btn').forEach(button => {
        button.addEventListener('click', async (event) => {
            const productId = parseInt(event.target.dataset.productId);
            const productName = event.target.dataset.productName;
            const productPrice = parseFloat(event.target.dataset.productPrice);
            const productImage = event.target.dataset.productImage;
            await addProductToCart(productId, 1, productName, productPrice, productImage);
        });
    });
}

async function loadCartAndRender() {
    const userId = getUserId();
    if (!userId) {
        document.getElementById('emptyCartMessage').style.display = 'block';
        document.getElementById('cartTableCard').style.display = 'none';
        document.getElementById('proceedToCheckoutBtn').disabled = true;
        updateCartTotals();
        return;
    }

    userCartId = await fetchUserCartId(userId);
    if (!userCartId) {
        document.getElementById('emptyCartMessage').style.display = 'block';
        document.getElementById('cartTableCard').style.display = 'none';
        document.getElementById('proceedToCheckoutBtn').disabled = true;
        updateCartTotals();
        return;
    }

    const itemsInCartDTO = await fetchCartItems(userCartId);
    cartItems = [];

    if (itemsInCartDTO.length === 0) {
        document.getElementById('emptyCartMessage').style.display = 'block';
        document.getElementById('cartTableCard').style.display = 'none';
        document.getElementById('proceedToCheckoutBtn').disabled = true;
    } else {
        document.getElementById('emptyCartMessage').style.display = 'none';
        document.getElementById('cartTableCard').style.display = 'block';
        document.getElementById('proceedToCheckoutBtn').disabled = false;

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
    const tableBody = document.getElementById('cartTableBody');
    if (!tableBody) return;

    tableBody.innerHTML = '';

    if (cartItems.length === 0) {
        document.getElementById('emptyCartMessage').style.display = 'block';
        document.getElementById('cartTableCard').style.display = 'none';
        document.getElementById('proceedToCheckoutBtn').disabled = true;
        return;
    } else {
        document.getElementById('emptyCartMessage').style.display = 'none';
        document.getElementById('cartTableCard').style.display = 'block';
        document.getElementById('proceedToCheckoutBtn').disabled = false;
    }

    cartItems.forEach(item => {
        const row = `
            <tr data-id="${item.id}" data-product-id="${item.idProducto}">
                <td class="align-middle text-center">
                    <div class="d-flex align-items-center justify-content-center">
                        <img src="${item.image}" alt="${item.name}" class="img-fluid me-2" style="max-width: 60px; height: auto;">
                        <span>${item.name}</span>
                    </div>
                </td>
                <td class="align-middle text-center">S/. ${item.price.toFixed(2)}</td>
                <td class="align-middle text-center">
                    <input type="number" class="form-control form-control-sm quantity-input mx-auto" value="${item.quantity}" min="1" style="width: 70px;">
                </td>
                <td class="align-middle text-center">S/. ${(item.price * item.quantity).toFixed(2)}</td>
                <td class="align-middle text-center">
                    <button class="btn btn-danger btn-sm remove-item-btn">
                        <i class="fas fa-trash-alt"></i>
                    </button>
                </td>
            </tr>
        `;
        tableBody.insertAdjacentHTML('beforeend', row);
    });

    addCartItemEventListeners();
}

function addCartItemEventListeners() {
    document.querySelectorAll('.remove-item-btn').forEach(button => {
        button.onclick = (event) => {
            const itemId = parseInt(event.target.closest('tr').dataset.id);
            if (confirm('¿Estás seguro de que quieres eliminar este producto del carrito?')) {
                removeItemFromCart(itemId);
            }
        };
    });

    document.querySelectorAll('.quantity-input').forEach(input => {
        input.onchange = (event) => {
            const itemId = parseInt(event.target.closest('tr').dataset.id);
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
}

function updateCartTotals() {
    let subtotal = 0;
    cartItems.forEach(item => {
        subtotal += item.price * item.quantity;
    });

    const igv = subtotal * IGV_RATE;
    const total = subtotal + igv;

    document.getElementById('cartSubtotal').textContent = `S/. ${subtotal.toFixed(2)}`;
    document.getElementById('cartIGV').textContent = `S/. ${igv.toFixed(2)}`;
    document.getElementById('cartTotal').textContent = `S/. ${total.toFixed(2)}`;

    document.getElementById('proceedToCheckoutBtn').disabled = cartItems.length === 0;
}

async function addProductToCart(productId, quantity = 1, productName, productPrice, productImage) {
    const userId = getUserId();
    if (!userId) return;

    if (!userCartId) {
        userCartId = await fetchUserCartId(userId);
        if (!userCartId) {
            showNotification('No se pudo inicializar el carrito para añadir el producto.', 'danger');
            return;
        }
    }

    try {
        const response = await fetch(`${API_BASE_URL}/carrito-anadidos`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                idCarrito: userCartId,
                idProducto: productId,
                cantidad: quantity
            })
        });

        if (response.status === 201) {
            showNotification(`${productName} añadido al carrito.`, 'success');
            await loadCartAndRender();
        } else {
            const errorText = await response.text();
            console.error('Error al añadir producto al carrito:', response.status, errorText);
            showNotification(`Error al añadir ${productName} al carrito: ${errorText}`, 'danger');
        }
    } catch (error) {
        console.error('Error de red al añadir producto al carrito:', error);
        showNotification('Error de red al añadir el producto al carrito.', 'danger');
    }
}

async function updateItemQuantity(itemId, newCantidad) {
    try {
        const response = await fetch(`${API_BASE_URL}/carrito-anadidos/${itemId}/cantidad/${newCantidad}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
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
        const response = await fetch(`${API_BASE_URL}/carrito-anadidos/${itemId}`, {
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
        const response = await fetch(`${API_BASE_URL}/carrito-anadidos/carrito/${userCartId}/clear`, {
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
    if (!userId) {
        showNotification('No se pudo procesar el pedido. ID de usuario no disponible.', 'danger');
        return;
    }

    if (cartItems.length === 0) {
        showNotification('Tu carrito está vacío. Agrega productos antes de proceder al pago.', 'warning');
        return;
    }

    try {
        const response = await fetch(`${API_BASE_URL}/pedidos/from-carrito/${userId}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
        });

        if (response.status === 201) {
            const createdPedido = await response.json();
            showNotification('¡Pedido realizado con éxito!', 'success');
            console.log('Pedido creado:', createdPedido);

            await loadCartAndRender();
            setTimeout(() => {
                window.location.href = 'Pedidos.html';
            }, 1500);

        } else if (response.status === 400) {
            const errorText = await response.text();
            showNotification(`Error al procesar el pedido: ${errorText}`, 'warning');
            console.error('Error al crear el pedido (bad request):', response.status, errorText);
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
    // renderProducts(); // Esta función solo debería llamarse en Index.html, no en Carrito.html

    const proceedToCheckoutBtn = document.getElementById('proceedToCheckoutBtn');
    if (proceedToCheckoutBtn) {
        proceedToCheckoutBtn.addEventListener('click', handleProceedToCheckout);
    }

    const clearCartBtn = document.getElementById('clearCartBtn');
    if (clearCartBtn) {
        clearCartBtn.addEventListener('click', () => {
            if (confirm('¿Estás seguro de que quieres vaciar todo tu carrito?')) {
                clearEntireCart();
            }
        });
    }

    // Lógica para mostrar/ocultar el botón de cerrar sesión en Carrito.html
    const logoutItem = document.getElementById('navLogoutItem');
    const accountLink = document.getElementById('navAccountLink');
    const userRole = localStorage.getItem('userRole');

    if (userRole) {
        if (logoutItem) logoutItem.style.display = 'block';
        if (accountLink) accountLink.style.display = 'none';

        const logoutBtn = document.getElementById('logoutButton');
        if (logoutBtn) {
            logoutBtn.addEventListener('click', (event) => {
                event.preventDefault();
                if (window.farmalineAuth && typeof window.farmalineAuth.logout === 'function') {
                    window.farmalineAuth.logout();
                } else {
                    localStorage.removeItem("farmalineUserId");
                    localStorage.removeItem("farmalineAdminId");
                    localStorage.removeItem("farmalineRepartidorId");
                    localStorage.removeItem("userRole");
                    window.location.href = "Login.html";
                }
            });
        }
    } else {
        if (logoutItem) logoutItem.style.display = 'none';
        if (accountLink) accountLink.style.display = 'block';
    }
});
