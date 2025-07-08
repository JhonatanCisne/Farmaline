let products = [];
let filteredProducts = [];
let currentProduct = null;
const bootstrap = window.bootstrap; 

const API_BASE_URL = "http://localhost:8080";
const PRODUCTS_API_ENDPOINT = `${API_BASE_URL}/api/productos`;
const LOTES_API_ENDPOINT = `${API_BASE_URL}/api/lotes-producto/producto`;
const ADD_TO_CART_API_ENDPOINT = `${API_BASE_URL}/api/carrito-anadidos`;
const USER_ID_STORAGE_KEY = 'farmalineUserId';

let modalProductImage, modalProductName, modalProductCategory, modalProductPrice,
    modalProductDescription, modalInfoList, qtyInput, decreaseQtyBtn, increaseQtyBtn,
    addToCartButtonModal, productModalElement, modalInstance;

document.addEventListener("DOMContentLoaded", () => {
    let alertPlaceholder = document.getElementById('alertPlaceholder');
    if (!alertPlaceholder) {
        alertPlaceholder = document.createElement('div');
        alertPlaceholder.id = 'alertPlaceholder';
        alertPlaceholder.style.cssText = 'position: fixed; top: 70px; right: 20px; z-index: 1050; width: 300px;';
        document.body.appendChild(alertPlaceholder);
        console.log('alertPlaceholder creado dinámicamente.');
    }
    initializeModalElements();

    fetchProductsFromBackend();
    setupEventListeners();
    checkLoginStatusAndSetupLogout();
    updateCartCountDisplay();
});

function initializeModalElements() {
    productModalElement = document.getElementById("productModal");
    if (!productModalElement) {
        console.error("Error: Elemento #productModal no encontrado.");
        return;
    }
    modalInstance = new bootstrap.Modal(productModalElement);

    modalImage = document.getElementById("modalProductImage");
    modalProductName = document.getElementById("modalProductName");
    modalProductCategory = document.getElementById("modalProductCategory");
    modalProductPrice = document.getElementById("modalProductPrice");
    modalProductDescription = document.getElementById("modalProductDescription");
    modalInfoList = document.getElementById("modalProductInfo");
    qtyInput = document.getElementById("quantity");
    decreaseQtyBtn = document.getElementById("decreaseQty");
    increaseQtyBtn = document.getElementById("increaseQty");
    addToCartButtonModal = document.getElementById("addToCart");

    if (!modalImage || !modalProductName || !modalProductCategory || !modalProductPrice ||
        !modalProductDescription || !modalInfoList || !qtyInput || !decreaseQtyBtn ||
        !increaseQtyBtn || !addToCartButtonModal) {
        console.error("Error: No se encontraron todos los elementos clave del modal. Revisa tus IDs en el HTML.");
    }

    setupModalButtonsListeners();
}

async function fetchProductsFromBackend() {
    console.log('Iniciando fetchProductsFromBackend...');
    try {
        const response = await fetch(PRODUCTS_API_ENDPOINT);
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        const data = await response.json();
        console.log('Productos obtenidos:', data);

        products = await Promise.all(data.map(async item => {
            const lotesResponse = await fetch(`${LOTES_API_ENDPOINT}/${item.idProducto}`);
            let totalStock = 0;

            if (lotesResponse.ok) {
                const lotes = await lotesResponse.json();
                const now = new Date();
                const today = new Date(now.getFullYear(), now.getMonth(), now.getDate());

                lotes.forEach(lote => {
                    const expiry = new Date(lote.fechaCaducidad);
                    if (expiry >= today) {
                        totalStock += lote.cantidadDisponible;
                    }
                });
                console.log(`Producto ${item.idProducto} - Stock calculado: ${totalStock}`);
            } else {
                console.warn(`No se pudieron cargar los lotes para el producto ${item.idProducto}. Usando stock predeterminado de 0.`);
            }

            return {
                id: item.idProducto,
                name: item.nombre.split(" ")[0], 
                fullName: item.nombre,
                category: item.categoria || 'general',
                price: item.precio,
                originalPrice: item.precioOriginal || null,
                image: `../images/P/${item.nombre.replace(/\s+/g, '')}C.png`, 
                description: item.descripcion || '',
                hasDiscount: item.tieneDescuento || false,
                isNew: item.esNuevo || false,
                info: item.informacionAdicional || [],
                stock: totalStock,
            };
        }));

        filteredProducts = [...products];
        renderProducts();
        showSlide(0);

    } catch (error) {
        console.error('Error fetching products or lots:', error);
        const container = document.getElementById("productsContainer");
        container.innerHTML = '<p class="text-danger">No se pudieron cargar los productos. Inténtalo de nuevo más tarde.</p>';
    }
}

function setupEventListeners() {
    console.log('Configurando event listeners principales...');
    document.querySelectorAll(".price-filter").forEach((radio) => {
        radio.addEventListener("change", applyFilters);
    });

    document.querySelector(".alpha-filter")?.addEventListener("change", applyFilters);

    document.getElementById("discountFilter")?.addEventListener("change", applyFilters);
    document.getElementById("newFilter")?.addEventListener("change", applyFilters);
    document.getElementById("stockFilter")?.addEventListener("change", applyFilters);

    document.querySelectorAll(".category-filter").forEach((link) => {
        link.addEventListener("click", function (e) {
            e.preventDefault();
            const category = this.dataset.category;
            filterByCategory(category);
        });
    });

    document.getElementById("searchInput")?.addEventListener("input", function () {
        const searchTerm = this.value.toLowerCase();
        searchProducts(searchTerm);
    });
}

function renderProducts() {
    console.log('Renderizando productos...');
    const container = document.getElementById("productsContainer");
    if (!container) {
        console.error("Error: Contenedor de productos no encontrado.");
        return;
    }
    container.innerHTML = "";

    if (filteredProducts.length === 0) {
        container.innerHTML = '<p class="text-muted col-12 text-center">No se encontraron productos con los filtros aplicados.</p>';
        return;
    }

    filteredProducts.forEach((product) => {
        const productCard = createProductCard(product);
        container.appendChild(productCard);
    });
}

function createProductCard(product) {
    const col = document.createElement("div");
    col.className = "col-md-4 mb-4";

    const badges = [];
    if (product.hasDiscount) badges.push('<span class="badge badge-discount me-1">DESCUENTO</span>');
    if (product.isNew) badges.push('<span class="badge bg-info me-1">NUEVO</span>');
    if (product.stock <= 0) badges.push('<span class="badge bg-secondary me-1">SIN STOCK</span>');

    const priceHTML = product.hasDiscount && product.originalPrice !== null ?
        `<p class="h5 fw-bold mb-0">S/. ${product.price.toFixed(2)} <small class="text-muted text-decoration-line-through">S/. ${product.originalPrice.toFixed(2)}</small></p>` :
        `<p class="h5 fw-bold mb-0">S/. ${product.price.toFixed(2)}</p>`;

    col.innerHTML = `
        <div class="card h-100 product-card" data-product-id="${product.id}">
            <div class="card-body">
                <div class="mb-3">
                    <span class="badge bg-danger mb-2">${product.category.toUpperCase()}:</span>
                    ${badges.join("")}
                </div>
                <div class="d-flex align-items-center mb-3">
                    <img src="${product.image}" alt="${product.fullName}" class="me-3" style="width: 80px; height: 80px; object-fit: contain;">
                    <div>
                        <h6 class="card-title small mb-1">${product.fullName}</h6>
                        ${priceHTML}
                    </div>
                </div>
                <button class="btn btn-teal w-100 rounded-pill add-to-cart-btn" data-product-id="${product.id}" ${product.stock <= 0 ? 'disabled' : ''}>
                    ${product.stock <= 0 ? 'SIN STOCK' : 'AÑADIR'}
                </button>
            </div>
        </div>
    `;

    const productCardElement = col.querySelector(".product-card");
    if (productCardElement) {
        productCardElement.addEventListener("click", (e) => {
            if (!e.target.closest(".add-to-cart-btn")) {
                console.log('Click en tarjeta de producto, abriendo modal para:', product.fullName);
                openProductModal(product);
            } else {
                console.log('Click en botón "Añadir" dentro de la tarjeta, manejando por separado.');
            }
        });
    }

    const addToCartButton = col.querySelector(".add-to-cart-btn");
    if (addToCartButton && product.stock > 0) {
        addToCartButton.addEventListener("click", (e) => {
            e.stopPropagation();
            console.log('Click en botón Añadir directo de la tarjeta para:', product.fullName);
            addToCart(product, 1); 
        });
    }

    return col;
}

function applyFilters() {
    let filtered = [...products];

    const currentCategory = getCurrentCategory();
    if (currentCategory && currentCategory !== "all") {
        filtered = filtered.filter((product) => product.category === currentCategory);
    }

    const discountFilterCheckbox = document.getElementById("discountFilter");
    if (discountFilterCheckbox && discountFilterCheckbox.checked) {
        filtered = filtered.filter((product) => product.hasDiscount);
    }

    const newFilterCheckbox = document.getElementById("newFilter");
    if (newFilterCheckbox && newFilterCheckbox.checked) {
        filtered = filtered.filter((product) => product.isNew);
    }

    const stockFilterCheckbox = document.getElementById("stockFilter");
    if (stockFilterCheckbox && stockFilterCheckbox.checked) {
        filtered = filtered.filter((product) => product.stock > 0);
    }

    const selectedSort = document.querySelector('input[name="priceFilter"]:checked')?.value;

    switch (selectedSort) {
        case "asc":
            filtered.sort((a, b) => a.price - b.price);
            break;
        case "desc":
            filtered.sort((a, b) => b.price - a.price);
            break;
        case "alpha":
            filtered.sort((a, b) => a.fullName.localeCompare(b.fullName));
            break;
        default:
            break;
    }

    filteredProducts = filtered;
    renderProducts();
}

function filterByCategory(category) {
    setCurrentCategory(category);
    applyFilters();
}

function searchProducts(searchTerm) {
    if (!searchTerm) {
        applyFilters(); 
        return;
    }

    filteredProducts = products.filter(
        (product) =>
        product.fullName.toLowerCase().includes(searchTerm) ||
        product.description.toLowerCase().includes(searchTerm) ||
        product.category.toLowerCase().includes(searchTerm)
    );

    renderProducts();
}

function getCurrentCategory() {
    return document.querySelector(".category-filter.active")?.dataset.category || "all";
}

function setCurrentCategory(category) {
    document.querySelectorAll(".category-filter").forEach((link) => {
        link.classList.remove("active");
    });
    const selectedCategoryLink = document.querySelector(`[data-category="${category}"]`);
    if (selectedCategoryLink) {
        selectedCategoryLink.classList.add("active");
    }
}

function openProductModal(product) {
    console.log('Abriendo modal para producto:', product);
    currentProduct = product; 

    if (!modalImage || !modalProductName || !modalProductCategory || !modalProductPrice ||
        !modalProductDescription || !modalInfoList || !qtyInput || !addToCartButtonModal || !modalInstance) {
        console.error("Error: Los elementos del modal no están inicializados. No se puede abrir el modal.");
        return;
    }

    modalImage.src = product.image;
    modalImage.alt = product.fullName;
    modalProductName.textContent = product.fullName;
    modalProductCategory.textContent = getCategoryName(product.category);

    const priceHTML = product.hasDiscount && product.originalPrice !== null ?
        `S/. ${product.price.toFixed(2)} <small class="text-muted text-decoration-line-through">S/. ${product.originalPrice.toFixed(2)}</small>` :
        `S/. ${product.price.toFixed(2)}`;
    modalProductPrice.innerHTML = priceHTML;

    modalProductDescription.textContent = product.description;

    modalInfoList.innerHTML = "";
    if (product.info && Array.isArray(product.info)) {
        product.info.forEach((info) => {
            const li = document.createElement("li");
            li.textContent = info;
            modalInfoList.appendChild(li);
        });
    } else {
        const li = document.createElement("li");
        li.textContent = "No hay información adicional disponible.";
        modalInfoList.appendChild(li);
    }

    qtyInput.value = 1;
    qtyInput.max = product.stock;
    qtyInput.oninput = () => {
        let val = parseInt(qtyInput.value);
        if (isNaN(val) || val < 1) {
            qtyInput.value = 1;
        } else if (val > product.stock) {
            qtyInput.value = product.stock;
        }
    };

    if (product.stock <= 0) {
        qtyInput.disabled = true;
        addToCartButtonModal.disabled = true;
        addToCartButtonModal.textContent = "SIN STOCK";
    } else {
        qtyInput.disabled = false;
        addToCartButtonModal.disabled = false;
        addToCartButtonModal.textContent = "Agregar al Carrito";
    }

    modalInstance.show();
    console.log('Modal intentando mostrarse.');
}

function setupModalButtonsListeners() {
    console.log('Configurando eventos de los botones del modal una sola vez...');

    decreaseQtyBtn?.addEventListener("click", () => {
        if (qtyInput) {
            let currentQty = Number.parseInt(qtyInput.value);
            if (currentQty > 1) {
                qtyInput.value = currentQty - 1;
            }
        }
    });

    increaseQtyBtn?.addEventListener("click", () => {
        if (qtyInput && currentProduct) {
            let currentQty = Number.parseInt(qtyInput.value);
            if (currentQty < currentProduct.stock) {
                qtyInput.value = currentQty + 1;
            }
        } else if (!currentProduct) {
            console.warn("No hay producto seleccionado para aumentar la cantidad.");
        }
    });

    addToCartButtonModal?.addEventListener("click", () => {
        if (currentProduct) {
            const quantity = Number.parseInt(qtyInput.value);

            // Validaciones de stock y cantidad
            if (isNaN(quantity) || quantity <= 0) {
                showNotification(`La cantidad debe ser un número válido y al menos 1.`, "warning");
                return;
            }
            if (quantity > currentProduct.stock) {
                showNotification(`No puedes añadir más de ${currentProduct.stock} unidades de ${currentProduct.fullName}.`, "warning");
                return;
            }

            console.log('Llamando a addToCart desde el modal con cantidad:', quantity);
            addToCart(currentProduct, quantity);
        } else {
            console.error("Error: currentProduct es null en el modal. No se puede añadir al carrito.");
            showNotification("No se puede añadir al carrito. Producto no seleccionado.", "danger");
        }
    });
}

async function addToCart(product, quantity = 1) {
    console.log('Iniciando addToCart para:', product.fullName, 'Cantidad:', quantity);
    const userId = localStorage.getItem(USER_ID_STORAGE_KEY);

    if (!userId) {
        showNotification("Por favor, inicia sesión para añadir productos al carrito.", "danger");
        console.log('Usuario no logueado. Cancelando addToCart.');
        return;
    }

    try {
        let userCartId = await fetchUserCartId(parseInt(userId));
        if (!userCartId) {
            showNotification('No se pudo inicializar el carrito para añadir el producto.', "danger");
            console.log('No se pudo obtener/crear el ID del carrito. Cancelando addToCart.');
            return;
        }

        const payload = {
            idProducto: product.id,
            idCarrito: userCartId,
            cantidad: quantity
        };
        console.log("Enviando al carrito:", payload);

        const response = await fetch(ADD_TO_CART_API_ENDPOINT, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(payload)
        });

        if (response.ok) {
            const data = await response.json();
            console.log('Producto añadido/actualizado en el carrito con éxito:', data);
            showNotification(`${product.fullName} (${quantity} unid.) agregado al carrito.`, "success"); // Notificación de éxito
            updateCartCountDisplay();
            if (modalInstance) {
                modalInstance.hide(); 
                console.log('Modal cerrado después de añadir al carrito.');
            }
        } else {
            const errorData = await response.json().catch(() => ({ message: 'No hay mensaje de error del servidor.' })); // Asegura que siempre haya un objeto
            const errorMessage = errorData.message || `Error: ${response.statusText}`;
            console.error('Error al añadir producto al carrito:', response.status, errorData);
            showNotification(`Error al añadir ${product.fullName} al carrito: ${errorMessage}`, "danger");
        }
    } catch (error) {
        console.error('Error de red al añadir producto al carrito:', error);
        showNotification('Ocurrió un error de conexión. Intenta de nuevo más tarde.', "danger");
    }
}

async function fetchUserCartId(userId) {
    console.log('Intentando obtener/crear carrito para userId:', userId);
    try {
        const response = await fetch(`${API_BASE_URL}/api/carritos/usuario/${userId}`);
        if (response.ok) {
            const cart = await response.json();
            console.log('Carrito existente obtenido:', cart.idCarrito);
            return cart.idCarrito;
        } else if (response.status === 404) {
            console.log('Carrito no encontrado para el usuario, creando nuevo...');
            const createResponse = await fetch(`${API_BASE_URL}/api/carritos`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ idUsuario: userId })
            });
            if (createResponse.ok) {
                const newCart = await createResponse.json();
                console.log('Nuevo carrito creado:', newCart.idCarrito);
                return newCart.idCarrito;
            } else {
                console.error('Error al crear el carrito:', createResponse.status, await createResponse.text());
                return null;
            }
        } else {
            console.error('Error al obtener el carrito:', response.status, await response.text());
            return null;
        }
    } catch (error) {
        console.error('Error de red al obtener/crear carrito:', error);
        return null;
    }
}

async function updateCartCountDisplay() {
    const cartItemCountSpan = document.getElementById('cartItemCount');
    const userId = localStorage.getItem(USER_ID_STORAGE_KEY);

    if (!userId || !cartItemCountSpan) {
        if(cartItemCountSpan) cartItemCountSpan.textContent = '0';
        return;
    }
    console.log('Actualizando contador del carrito para userId:', userId);

    try {
        const userCartId = await fetchUserCartId(parseInt(userId));
        if (!userCartId) {
            cartItemCountSpan.textContent = '0';
            return;
        }

        const response = await fetch(`${API_BASE_URL}/api/carrito-anadidos/carrito/${userCartId}`);
        if (response.ok) {
            const text = await response.text();
            const items = text ? JSON.parse(text) : [];
            const totalItems = items.reduce((sum, item) => sum + item.cantidad, 0);
            cartItemCountSpan.textContent = totalItems.toString();
            console.log('Contador del carrito actualizado a:', totalItems);
        } else if (response.status === 204) {
            cartItemCountSpan.textContent = '0';
            console.log('Carrito vacío (204 No Content). Contador a 0.');
        } else {
            console.error('Error al obtener ítems del carrito para el contador:', response.status, await response.text());
            cartItemCountSpan.textContent = '0';
        }
    } catch (error) {
        console.error('Error de red al actualizar contador del carrito:', error);
        cartItemCountSpan.textContent = '0';
    }
}

function checkLoginStatusAndSetupLogout() {
    const logoutItem = document.getElementById('navLogoutItem');
    const accountLinkLi = document.querySelector('.navbar-nav .nav-item:nth-last-child(2)'); // Ajusta selector si es necesario

    const userId = localStorage.getItem(USER_ID_STORAGE_KEY);
    const adminId = localStorage.getItem('farmalineAdminId');
    const repartidorId = localStorage.getItem('farmalineRepartidorId');

    if (userId || adminId || repartidorId) {
        if (logoutItem) logoutItem.style.display = 'block';
        if (accountLinkLi) accountLinkLi.style.display = 'none';

        const logoutBtn = document.getElementById('logoutButton');
    } else {
        if (logoutItem) logoutItem.style.display = 'none';
        if (accountLinkLi) accountLinkLi.style.display = 'block';
    }
}

let currentSlide = 0;
const slides = document.querySelectorAll(".carousel-slide");
const indicators = document.querySelectorAll(".indicator");
const totalSlides = slides.length;

function showSlide(index) {
    if (slides.length === 0 || indicators.length === 0) {
        return;
    }

    slides.forEach((slide) => slide.classList.remove("active"));
    indicators.forEach((indicator) => indicator.classList.remove("active"));

    // Asegurarse de que el índice esté dentro de los límites
    const normalizedIndex = (index + totalSlides) % totalSlides;
    slides[normalizedIndex].classList.add("active");
    indicators[normalizedIndex].classList.add("active");

    currentSlide = normalizedIndex;
}

function nextSlide() {
    const next = (currentSlide + 1) % totalSlides;
    showSlide(next);
}

function prevSlide() {
    const prev = (currentSlide - 1 + totalSlides) % totalSlides;
    showSlide(prev);
}

const nextSlideBtn = document.getElementById("nextSlide");
const prevSlideBtn = document.getElementById("prevSlide");

if (nextSlideBtn) {
    nextSlideBtn.addEventListener("click", nextSlide);
}
if (prevSlideBtn) {
    prevSlideBtn.addEventListener("click", prevSlide);
}

indicators.forEach((indicator, index) => {
    indicator.addEventListener("click", () => showSlide(index));
});

document.querySelectorAll(".buy-btn").forEach((button) => {
    button.addEventListener("click", function () {
        const productId = Number.parseInt(this.dataset.productId);
        const product = products.find((p) => p.id === productId);

        if (product) {
            console.log('Click en botón "Comprar" del carrusel, abriendo modal para:', product.fullName);
            openProductModal(product);
        }
    });
});

let autoPlayInterval = setInterval(nextSlide, 5000);

const heroBanner = document.querySelector(".hero-banner");
if (heroBanner) {
    heroBanner.addEventListener("mouseenter", () => {
        clearInterval(autoPlayInterval);
    });

    heroBanner.addEventListener("mouseleave", () => {
        autoPlayInterval = setInterval(nextSlide, 5000);
    });
}

document.addEventListener("keydown", (e) => {
    if (e.key === "ArrowLeft") {
        prevSlide();
    } else if (e.key === "ArrowRight") {
        nextSlide();
    }
});

function getCategoryName(category) {
    const categoryNames = {
        medicamentos: "Medicamentos",
        vitaminas: "Vitaminas y Suplementos",
        "cuidado-cabello": "Cuidado del Cabello",
        bebes: "Bebés y Niños",
        fotoproteccion: "Fotoprotección",
        "cuidado-personal": "Cuidado Personal",
    };
    return categoryNames[category] || "General";
}

function showNotification(message, type) {
    const alertPlaceholder = document.getElementById('alertPlaceholder');
    if (!alertPlaceholder) {
        console.error("alertPlaceholder no encontrado, la notificación no se mostrará.");
        return;
    }

    const wrapper = document.createElement('div');
    wrapper.innerHTML = [
        `<div class="alert alert-${type} alert-dismissible fade show" role="alert">`,
        ` <div>${message}</div>`,
        ' <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>',
        '</div>'
    ].join('');

    alertPlaceholder.append(wrapper);

    setTimeout(() => {
        const alertElement = wrapper.querySelector('.alert');
        const bsAlert = bootstrap.Alert.getInstance(alertElement);
        if (bsAlert) {
            bsAlert.dispose(); 
        } else {
            wrapper.remove();
        }
    }, 5000);
}