let products = [];
let filteredProducts = [];
let currentProduct = null;
const bootstrap = window.bootstrap;

const API_BASE_URL = "http://localhost:8080";
const PRODUCTS_API_ENDPOINT = `${API_BASE_URL}/api/productos`;
const ADD_TO_CART_API_ENDPOINT = `${API_BASE_URL}/api/carrito-anadido`;

document.addEventListener("DOMContentLoaded", () => {
    fetchProductsFromBackend();
    setupEventListeners();
});

async function fetchProductsFromBackend() {
    try {
        const response = await fetch(PRODUCTS_API_ENDPOINT);
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        const data = await response.json();

        products = data.map(item => ({
            id: item.idProducto,
            name: item.nombre.split(" ")[0],
            fullName: item.nombre,
            category: item.categoria || 'general',
            price: item.precio,
            originalPrice: item.precioOriginal || null,
            image: `${item.imagen}`,
            description: item.descripcion || '',
            hasDiscount: item.tieneDescuento || false,
            info: item.informacionAdicional || [],
            comments: item.comentarios || []
        }));

        filteredProducts = [...products];
        renderProducts();
        showSlide(0);

    } catch (error) {
        console.error('Error fetching products:', error);
        const container = document.getElementById("productsContainer");
        container.innerHTML = '<p class="text-danger">No se pudieron cargar los productos. Inténtalo de nuevo más tarde.</p>';
    }
}

function setupEventListeners() {
    document.querySelectorAll(".price-filter").forEach((radio) => {
        radio.addEventListener("change", applyFilters);
    });

    document.querySelector(".alpha-filter").addEventListener("change", applyFilters);

    document.getElementById("discountFilter").addEventListener("change", applyFilters);
    document.getElementById("newFilter").addEventListener("change", applyFilters);

    document.querySelectorAll(".category-filter").forEach((link) => {
        link.addEventListener("click", function (e) {
            e.preventDefault();
            const category = this.dataset.category;
            filterByCategory(category);
        });
    });

    document.getElementById("searchInput").addEventListener("input", function () {
        const searchTerm = this.value.toLowerCase();
        searchProducts(searchTerm);
    });

    setupModalEvents();
}

function renderProducts() {
    const container = document.getElementById("productsContainer");
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
    if (product.isNew) badges.push('<span class="badge badge-new me-1">NUEVO</span>');
    if (product.hasDiscount) badges.push('<span class="badge badge-discount me-1">DESCUENTO</span>');

    const priceHTML = product.hasDiscount && product.originalPrice !== null ?
        `<p class="h5 fw-bold mb-0">S/. ${product.price.toFixed(2)} <small class="text-muted text-decoration-line-through">S/. ${product.originalPrice.toFixed(2)}</small></p>` :
        `<p class="h5 fw-bold mb-0">S/. ${product.price.toFixed(2)}</p>`;

    col.innerHTML = `
        <div class="card h-100 product-card" data-product-id="${product.id}">
            <div class="card-body">
                <div class="mb-3">
                    <span class="badge bg-danger mb-2">${product.name.split(" ")[0].toUpperCase()}:</span>
                    ${badges.join("")}
                </div>
                <div class="d-flex align-items-center mb-3">
                    <img src="${product.image}" alt="${product.fullName}" class="me-3" style="width: 80px; height: 80px; object-fit: contain;">
                    <div>
                        <h6 class="card-title small mb-1">${product.fullName}</h6>
                        ${priceHTML}
                    </div>
                </div>
                <button class="btn btn-teal w-100 rounded-pill add-to-cart-btn" data-product-id="${product.id}">AÑADIR</button>
            </div>
        </div>
    `;

    col.querySelector(".product-card").addEventListener("click", (e) => {
        if (!e.target.classList.contains("add-to-cart-btn")) {
            openProductModal(product);
        }
    });

    col.querySelector(".add-to-cart-btn").addEventListener("click", (e) => {
        e.stopPropagation();
        addToCart(product);
    });

    return col;
}

function applyFilters() {
    let filtered = [...products];

    const currentCategory = getCurrentCategory();
    if (currentCategory && currentCategory !== "all") {
        filtered = filtered.filter((product) => product.category === currentCategory);
    }

    if (document.getElementById("discountFilter").checked) {
        filtered = filtered.filter((product) => product.hasDiscount);
    }

    if (document.getElementById("newFilter").checked) {
        filtered = filtered.filter((product) => product.isNew);
    }

    const selectedSort = document.querySelector('input[name="priceFilter"]:checked').value;

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
    document.querySelector(`[data-category="${category}"]`)?.classList.add("active");
}

function openProductModal(product) {
    currentProduct = product;

    document.getElementById("modalProductImage").src = product.image;
    document.getElementById("modalProductImage").alt = product.fullName;
    document.getElementById("modalProductName").textContent = product.fullName;
    document.getElementById("modalProductCategory").textContent = getCategoryName(product.category);

    const priceHTML = product.hasDiscount && product.originalPrice !== null ?
        `S/. ${product.price.toFixed(2)} <small class="text-muted text-decoration-line-through">S/. ${product.originalPrice.toFixed(2)}</small>` :
        `S/. ${product.price.toFixed(2)}`;
    document.getElementById("modalProductPrice").innerHTML = priceHTML;

    document.getElementById("modalProductDescription").textContent = product.description;

    const infoList = document.getElementById("modalProductInfo");
    infoList.innerHTML = "";
    product.info.forEach((info) => {
        const li = document.createElement("li");
        li.textContent = info;
        infoList.appendChild(li);
    });

    loadComments(product);

    const modal = new bootstrap.Modal(document.getElementById("productModal"));
    modal.show();
}

function setupModalEvents() {
    document.getElementById("decreaseQty").addEventListener("click", () => {
        const qtyInput = document.getElementById("quantity");
        const currentQty = Number.parseInt(qtyInput.value);
        if (currentQty > 1) {
            qtyInput.value = currentQty - 1;
        }
    });

    document.getElementById("increaseQty").addEventListener("click", () => {
        const qtyInput = document.getElementById("quantity");
        const currentQty = Number.parseInt(qtyInput.value);
        qtyInput.value = currentQty + 1;
    });

    document.querySelectorAll(".star").forEach((star) => {
        star.addEventListener("click", function () {
            const rating = Number.parseInt(this.dataset.rating);
            document.getElementById("selectedRating").value = rating;
            updateStarDisplay(rating);
        });

        star.addEventListener("mouseenter", function () {
            const rating = Number.parseInt(this.dataset.rating);
            updateStarDisplay(rating);
        });
    });

    document.querySelector(".rating-stars").addEventListener("mouseleave", () => {
        const selectedRating = Number.parseInt(document.getElementById("selectedRating").value);
        updateStarDisplay(selectedRating);
    });

    document.getElementById("addComment").addEventListener("click", addComment);

    document.getElementById("addToCart").addEventListener("click", () => {
        if (currentProduct) {
            const quantity = Number.parseInt(document.getElementById("quantity").value);
            addToCart(currentProduct, quantity);
        } else {
            console.error("Error: currentProduct es null en el modal. No se puede añadir al carrito.");
        }
    });
}

function updateStarDisplay(rating) {
    document.querySelectorAll(".star").forEach((star, index) => {
        if (index < rating) {
            star.classList.add("active");
        } else {
            star.classList.remove("active");
        }
    });
}

function loadComments(product) {
    const commentsList = document.getElementById("commentsList");
    commentsList.innerHTML = "";

    if (product.comments.length === 0) {
        commentsList.innerHTML = '<p class="text-muted">No hay comentarios aún. ¡Sé el primero en comentar!</p>';
        return;
    }

    product.comments.forEach((comment) => {
        const commentDiv = document.createElement("div");
        commentDiv.className = "comment-item";

        const stars = "★".repeat(comment.rating) + "☆".repeat(5 - comment.rating);

        commentDiv.innerHTML = `
            <div class="comment-rating">${stars}</div>
            <div class="d-flex justify-content-between">
                <span class="comment-author">${comment.author}</span>
                <span class="comment-date">${formatDate(comment.date)}</span>
            </div>
            <div class="comment-text">${comment.text}</div>
        `;

        commentsList.appendChild(commentDiv);
    });
}

function addComment() {
    const author = document.getElementById("commentAuthor").value.trim();
    const text = document.getElementById("commentText").value.trim();
    const rating = Number.parseInt(document.getElementById("selectedRating").value);

    if (!author || !text || rating === 0) {
        alert("Por favor completa todos los campos y selecciona una calificación.");
        return;
    }

    const newComment = {
        author: author,
        rating: rating,
        text: text,
        date: new Date().toISOString().split("T")[0],
    };

    currentProduct.comments.push(newComment);

    document.getElementById("commentAuthor").value = "";
    document.getElementById("commentText").value = "";
    document.getElementById("selectedRating").value = "0";
    updateStarDisplay(0);

    loadComments(currentProduct);
}

async function addToCart(product, quantity = 1) {
    const userId = localStorage.getItem("farmalineUserId");

    if (!userId) {
        showNotification("Por favor, inicia sesión para añadir productos al carrito.", "danger");
        return;
    }

    try {
        const payload = {
            idProducto: product.id,
            idCarrito: parseInt(userId),
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
            console.log('Producto añadido/actualizado en el carrito:', data);
            showNotification(`${product.fullName} (${quantity} unid.) agregado al carrito.`, "success");
        } else {
            const errorData = await response.json();
            const errorMessage = errorData.message || `Error: ${response.statusText}`;
            console.error('Error al añadir producto al carrito:', response.status, errorData);
            showNotification(`Error al añadir ${product.fullName} al carrito: ${errorMessage}`, "danger");
        }
    } catch (error) {
        console.error('Error de red al añadir producto al carrito:', error);
        showNotification('Ocurrió un error de conexión. Intenta de nuevo más tarde.', "danger");
    }
}

function showNotification(message, type = "success") {
    const notification = document.createElement("div");
    notification.className = `alert alert-${type} position-fixed`;
    notification.style.cssText = "top: 20px; right: 20px; z-index: 9999; min-width: 300px; animation: fadeIn 0.5s, fadeOut 0.5s 2.5s forwards;";
    notification.innerHTML = `
        <i class="fas ${type === 'success' ? 'fa-check-circle' : 'fa-exclamation-triangle'} me-2"></i>
        ${message}
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    `;

    document.body.appendChild(notification);

    const style = document.createElement('style');
    style.innerHTML = `
        @keyframes fadeIn {
            from { opacity: 0; transform: translateY(-20px); }
            to { opacity: 1; transform: translateY(0); }
        }
        @keyframes fadeOut {
            from { opacity: 1; transform: translateY(0); }
            to { opacity: 0; transform: translateY(-20px); }
        }
    `;
    document.head.appendChild(style);

    setTimeout(() => {
        if (notification.parentNode) {
            notification.remove();
            style.remove();
        }
    }, 3000);
}

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

function formatDate(dateString) {
    const date = new Date(dateString);
    return date.toLocaleDateString("es-ES", {
        year: "numeric",
        month: "long",
        day: "numeric",
    });
}

let currentSlide = 0;
const slides = document.querySelectorAll(".carousel-slide");
const indicators = document.querySelectorAll(".indicator");
const totalSlides = slides.length;


function showSlide(index) {
    slides.forEach((slide) => slide.classList.remove("active"));
    indicators.forEach((indicator) => indicator.classList.remove("active"));

    slides[index].classList.add("active");
    indicators[index].classList.add("active");

    currentSlide = index;
}

function nextSlide() {
    const next = (currentSlide + 1) % totalSlides;
    showSlide(next);
}

function prevSlide() {
    const prev = (currentSlide - 1 + totalSlides) % totalSlides;
    showSlide(prev);
}

document.getElementById("nextSlide").addEventListener("click", nextSlide);
document.getElementById("prevSlide").addEventListener("click", prevSlide);

indicators.forEach((indicator, index) => {
    indicator.addEventListener("click", () => showSlide(index));
});

document.querySelectorAll(".buy-btn").forEach((button) => {
    button.addEventListener("click", function () {
        const productId = Number.parseInt(this.dataset.productId);
        const product = products.find((p) => p.id === productId);

        if (product) {
            openProductModal(product);
        }
    });
});

let autoPlayInterval = setInterval(nextSlide, 5000);

document.querySelector(".hero-banner").addEventListener("mouseenter", () => {
    clearInterval(autoPlayInterval);
});

document.querySelector(".hero-banner").addEventListener("mouseleave", () => {
    autoPlayInterval = setInterval(nextSlide, 5000);
});

document.addEventListener("keydown", (e) => {
    if (e.key === "ArrowLeft") {
        prevSlide();
    } else if (e.key === "ArrowRight") {
        nextSlide();
    }
});