document.addEventListener('DOMContentLoaded', function() {
    const toggleSidebarBtn = document.getElementById('toggle-sidebar');
    const sidebar = document.getElementById('sidebar');
    const navLinks = document.querySelectorAll('.sidebar-nav .nav-link');
    const contentSections = document.querySelectorAll('.content-section');
    const pageTitle = document.getElementById('page-title');

    function updatePageTitle(title) {
        if (pageTitle) {
            pageTitle.textContent = title;
        }
    }

    if (toggleSidebarBtn && sidebar) {
        toggleSidebarBtn.addEventListener('click', function() {
            sidebar.classList.toggle('active');
            document.body.classList.toggle('sidebar-open');
        });
    }

    navLinks.forEach(link => {
        if (link.dataset.section === undefined) {
            return;
        }

        link.addEventListener('click', function(event) {
            event.preventDefault();

            navLinks.forEach(item => {
                if (item.dataset.section !== undefined) {
                    item.classList.remove('active');
                }
            });
            contentSections.forEach(section => section.classList.remove('active'));

            this.classList.add('active');

            const targetSectionId = this.dataset.section;
            const targetSection = document.getElementById(targetSectionId + '-section');

            if (targetSection) {
                targetSection.classList.add('active');
                updatePageTitle(this.querySelector('span').textContent);
            }

            if (window.innerWidth <= 992 && sidebar.classList.contains('active')) {
                sidebar.classList.remove('active');
                document.body.classList.remove('sidebar-open');
            }
        });
    });

    const initialActiveLink = document.querySelector('.sidebar-nav .nav-link.active[data-section]');
    if (initialActiveLink) {
        const initialTargetSectionId = initialActiveLink.dataset.section;
        const initialTargetSection = document.getElementById(initialTargetSectionId + '-section');
        if (initialTargetSection) {
            initialTargetSection.classList.add('active');
            updatePageTitle(initialActiveLink.querySelector('span').textContent);
        }
    } else {
        const firstNavLink = document.querySelector('.sidebar-nav .nav-link[data-section="inicio"]');
        if (firstNavLink) {
            firstNavLink.classList.add('active');
            const firstContentSection = document.getElementById('inicio-section');
            if (firstContentSection) {
                firstContentSection.classList.add('active');
                updatePageTitle(firstNavLink.querySelector('span').textContent);
            }
        }
    }

    document.body.addEventListener('click', function(event) {
        if (sidebar.classList.contains('active') && 
            !sidebar.contains(event.target) && 
            (toggleSidebarBtn && !toggleSidebarBtn.contains(event.target))) {
            
            sidebar.classList.remove('active');
            document.body.classList.remove('sidebar-open');
        }
    });

    window.addEventListener('resize', function() {
        if (window.innerWidth > 992) {
            sidebar.classList.remove('active');
            document.body.classList.remove('sidebar-open');
        }
    });
});

document.addEventListener('DOMContentLoaded', function() {
    const toggleSidebarBtn = document.getElementById('toggle-sidebar');
    const sidebar = document.getElementById('sidebar');
    const navLinks = document.querySelectorAll('.sidebar-nav .nav-link');
    const contentSections = document.querySelectorAll('.content-section');
    const pageTitle = document.getElementById('page-title');
    const tablaProductosBody = document.getElementById('tabla-productos');

    function updatePageTitle(title) {
        if (pageTitle) {
            pageTitle.textContent = title;
        }
    }

    async function cargarProductos() {
        try {
            const response = await fetch('http://localhost:8080/api/productos');
            if (!response.ok) {
                if (response.status === 204) {
                    tablaProductosBody.innerHTML = '<tr><td colspan="11" class="text-center py-4">No hay productos disponibles.</td></tr>';
                    return;
                }
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            const productos = await response.json();
            
            tablaProductosBody.innerHTML = ''; // Limpiar tabla antes de añadir nuevos datos

            if (productos.length === 0) {
                tablaProductosBody.innerHTML = '<tr><td colspan="11" class="text-center py-4">No hay productos disponibles.</td></tr>';
                return;
            }

            productos.forEach(producto => {
                const row = document.createElement('tr');
                row.innerHTML = `
                    <td>${producto.idProducto}</td>
                    <td>${producto.nombre}</td>
                    <td>${producto.descripcion}</td>
                    <td>${producto.stockDisponible}</td>
                    <td>$${producto.precio.toFixed(2)}</td>
                    <td><img src="${producto.imagen}" alt="${producto.nombre}" style="width: 50px; height: 50px; object-fit: cover; border-radius: 4px;"></td>
                    <td>${producto.fechaCaducidad}</td>
                    <td>${producto.fechaIngreso}</td>
                    <td>${(producto.igv * 100).toFixed(0)}%</td>
                    <td>$${producto.precioFinal.toFixed(2)}</td>
                    <td>
                        <button class="btn btn-sm btn-info edit-btn" data-id="${producto.idProducto}"><i class="fas fa-edit"></i></button>
                        <button class="btn btn-sm btn-danger delete-btn" data-id="${producto.idProducto}"><i class="fas fa-trash-alt"></i></button>
                    </td>
                `;
                tablaProductosBody.appendChild(row);
            });
        } catch (error) {
            console.error('Error al cargar productos:', error);
            tablaProductosBody.innerHTML = '<tr><td colspan="11" class="text-center py-4 text-danger">Error al cargar los productos. Intente de nuevo más tarde.</td></tr>';
        }
    }

    if (toggleSidebarBtn && sidebar) {
        toggleSidebarBtn.addEventListener('click', function() {
            sidebar.classList.toggle('active');
            document.body.classList.toggle('sidebar-open');
        });
    }

    navLinks.forEach(link => {
        if (link.dataset.section === undefined) {
            return;
        }

        link.addEventListener('click', function(event) {
            event.preventDefault();

            navLinks.forEach(item => {
                if (item.dataset.section !== undefined) {
                    item.classList.remove('active');
                }
            });
            contentSections.forEach(section => section.classList.remove('active'));

            this.classList.add('active');

            const targetSectionId = this.dataset.section;
            const targetSection = document.getElementById(targetSectionId + '-section');

            if (targetSection) {
                targetSection.classList.add('active');
                updatePageTitle(this.querySelector('span').textContent);

                // Cargar productos si la sección activa es 'productos'
                if (targetSectionId === 'productos') {
                    cargarProductos();
                }
            }

            if (window.innerWidth <= 992 && sidebar.classList.contains('active')) {
                sidebar.classList.remove('active');
                document.body.classList.remove('sidebar-open');
            }
        });
    });

    const initialActiveLink = document.querySelector('.sidebar-nav .nav-link.active[data-section]');
    if (initialActiveLink) {
        const initialTargetSectionId = initialActiveLink.dataset.section;
        const initialTargetSection = document.getElementById(initialTargetSectionId + '-section');
        if (initialTargetSection) {
            initialTargetSection.classList.add('active');
            updatePageTitle(initialActiveLink.querySelector('span').textContent);
            if (initialTargetSectionId === 'productos') {
                cargarProductos();
            }
        }
    } else {
        const firstNavLink = document.querySelector('.sidebar-nav .nav-link[data-section="inicio"]');
        if (firstNavLink) {
            firstNavLink.classList.add('active');
            const firstContentSection = document.getElementById('inicio-section');
            if (firstContentSection) {
                firstContentSection.classList.add('active');
                updatePageTitle(firstNavLink.querySelector('span').textContent);
            }
        }
    }

    document.body.addEventListener('click', function(event) {
        if (sidebar.classList.contains('active') && 
            !sidebar.contains(event.target) && 
            (toggleSidebarBtn && !toggleSidebarBtn.contains(event.target))) {
            
            sidebar.classList.remove('active');
            document.body.classList.remove('sidebar-open');
        }
    });

    window.addEventListener('resize', function() {
        if (window.innerWidth > 992) {
            sidebar.classList.remove('active');
            document.body.classList.remove('sidebar-open');
        }
    });
});

document.addEventListener('DOMContentLoaded', function() {
    const toggleSidebarBtn = document.getElementById('toggle-sidebar');
    const sidebar = document.getElementById('sidebar');
    const navLinks = document.querySelectorAll('.sidebar-nav .nav-link');
    const contentSections = document.querySelectorAll('.content-section');
    const pageTitle = document.getElementById('page-title');
    const tablaProductosBody = document.getElementById('tabla-productos');

    const filterNombreInput = document.getElementById('filter-nombre');
    const filterCantidadInput = document.getElementById('filter-cantidad');
    const filterCantidadOpcionSelect = document.getElementById('filter-cantidad-opcion');
    const filterFechaCaducidadInput = document.getElementById('filter-fecha-caducidad');
    const applyFiltersBtn = document.getElementById('apply-filters-btn');
    const clearFiltersBtn = document.getElementById('clear-filters-btn');
    const filterIndicator = document.getElementById('filter-indicator');

    function updatePageTitle(title) {
        if (pageTitle) {
            pageTitle.textContent = title;
        }
    }

    async function cargarProductos(filtros = {}) {
        try {
            const url = new URL('http://localhost:8080/api/productos');
            if (filtros.nombre) {
                url.searchParams.append('nombre', filtros.nombre);
            }
            if (filtros.cantidad !== undefined && filtros.cantidad !== null && filtros.cantidad !== '') {
                if (filtros.cantidadOpcion === 'mayor') {
                    url.searchParams.append('stockMinimo', filtros.cantidad);
                } else if (filtros.cantidadOpcion === 'menor') {
                    url.searchParams.append('stockMaximo', filtros.cantidad);
                }
            }
            if (filtros.fechaCaducidad) {
                url.searchParams.append('fechaCaducidadHasta', filtros.fechaCaducidad);
            }

            const response = await fetch(url);
            if (!response.ok) {
                if (response.status === 204) {
                    tablaProductosBody.innerHTML = '<tr><td colspan="11" class="text-center py-4">No hay productos disponibles.</td></tr>';
                    return;
                }
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            const productos = await response.json();
            
            tablaProductosBody.innerHTML = ''; 

            if (productos.length === 0) {
                tablaProductosBody.innerHTML = '<tr><td colspan="11" class="text-center py-4">No hay productos disponibles con estos filtros.</td></tr>';
                return;
            }

            productos.forEach(producto => {
                const row = document.createElement('tr');
                row.innerHTML = `
                    <td>${producto.idProducto}</td>
                    <td>${producto.nombre}</td>
                    <td>${producto.descripcion}</td>
                    <td>${producto.stockDisponible}</td>
                    <td>$${producto.precio.toFixed(2)}</td>
                    <td><img src="${producto.imagen}" alt="${producto.nombre}" style="width: 50px; height: 50px; object-fit: cover; border-radius: 4px;"></td>
                    <td>${producto.fechaCaducidad}</td>
                    <td>${producto.fechaIngreso}</td>
                    <td>${(producto.igv * 100).toFixed(0)}%</td>
                    <td>$${producto.precioFinal.toFixed(2)}</td>
                    <td>
                        <button class="btn btn-sm btn-info edit-btn" data-id="${producto.idProducto}"><i class="fas fa-edit"></i></button>
                        <button class="btn btn-sm btn-danger delete-btn" data-id="${producto.idProducto}"><i class="fas fa-trash-alt"></i></button>
                    </td>
                `;
                tablaProductosBody.appendChild(row);
            });
        } catch (error) {
            console.error('Error al cargar productos:', error);
            tablaProductosBody.innerHTML = '<tr><td colspan="11" class="text-center py-4 text-danger">Error al cargar los productos. Intente de nuevo más tarde.</td></tr>';
        }
    }

    function aplicarFiltros() {
        const filtros = {};
        let indicadorTexto = '';

        if (filterNombreInput.value.trim() !== '') {
            filtros.nombre = filterNombreInput.value.trim();
            indicadorTexto += `Nombre: "${filtros.nombre}" `;
        }
        if (filterCantidadInput.value !== '') {
            filtros.cantidad = parseFloat(filterCantidadInput.value);
            filtros.cantidadOpcion = filterCantidadOpcionSelect.value;
            indicadorTexto += `Cantidad ${filtros.cantidadOpcion === 'mayor' ? '>=' : '<='} ${filtros.cantidad} `;
        }
        if (filterFechaCaducidadInput.value !== '') {
            filtros.fechaCaducidad = filterFechaCaducidadInput.value;
            indicadorTexto += `Caducidad hasta: ${filtros.fechaCaducidad} `;
        }

        if (indicadorTexto === '') {
            filterIndicator.textContent = 'Sin filtros aplicados';
        } else {
            filterIndicator.textContent = `Filtros: ${indicadorTexto.trim()}`;
        }
        
        cargarProductos(filtros);
    }

    function limpiarFiltros() {
        filterNombreInput.value = '';
        filterCantidadInput.value = '';
        filterCantidadOpcionSelect.value = 'mayor';
        filterFechaCaducidadInput.value = '';
        filterIndicator.textContent = 'Sin filtros aplicados';
        cargarProductos({});
    }

    if (toggleSidebarBtn && sidebar) {
        toggleSidebarBtn.addEventListener('click', function() {
            sidebar.classList.toggle('active');
            document.body.classList.toggle('sidebar-open');
        });
    }

    navLinks.forEach(link => {
        if (link.dataset.section === undefined) {
            return;
        }

        link.addEventListener('click', function(event) {
            event.preventDefault();

            navLinks.forEach(item => {
                if (item.dataset.section !== undefined) {
                    item.classList.remove('active');
                }
            });
            contentSections.forEach(section => section.classList.remove('active'));

            this.classList.add('active');

            const targetSectionId = this.dataset.section;
            const targetSection = document.getElementById(targetSectionId + '-section');

            if (targetSection) {
                targetSection.classList.add('active');
                updatePageTitle(this.querySelector('span').textContent);

                if (targetSectionId === 'productos') {
                    limpiarFiltros(); 
                }
            }

            if (window.innerWidth <= 992 && sidebar.classList.contains('active')) {
                sidebar.classList.remove('active');
                document.body.classList.remove('sidebar-open');
            }
        });
    });

    if (applyFiltersBtn) {
        applyFiltersBtn.addEventListener('click', aplicarFiltros);
    }

    if (clearFiltersBtn) {
        clearFiltersBtn.addEventListener('click', limpiarFiltros);
    }

    const initialActiveLink = document.querySelector('.sidebar-nav .nav-link.active[data-section]');
    if (initialActiveLink) {
        const initialTargetSectionId = initialActiveLink.dataset.section;
        const initialTargetSection = document.getElementById(initialTargetSectionId + '-section');
        if (initialTargetSection) {
            initialTargetSection.classList.add('active');
            updatePageTitle(initialActiveLink.querySelector('span').textContent);
            if (initialTargetSectionId === 'productos') {
                limpiarFiltros();
            }
        }
    } else {
        const firstNavLink = document.querySelector('.sidebar-nav .nav-link[data-section="inicio"]');
        if (firstNavLink) {
            firstNavLink.classList.add('active');
            const firstContentSection = document.getElementById('inicio-section');
            if (firstContentSection) {
                firstContentSection.classList.add('active');
                updatePageTitle(firstNavLink.querySelector('span').textContent);
            }
        }
    }

    document.body.addEventListener('click', function(event) {
        if (sidebar.classList.contains('active') && 
            !sidebar.contains(event.target) && 
            (toggleSidebarBtn && !toggleSidebarBtn.contains(event.target))) {
            
            sidebar.classList.remove('active');
            document.body.classList.remove('sidebar-open');
        }
    });

    window.addEventListener('resize', function() {
        if (window.innerWidth > 992) {
            sidebar.classList.remove('active');
            document.body.classList.remove('sidebar-open');
        }
    });
});

document.addEventListener('DOMContentLoaded', function() {
    const adminPanel = {
        API_BASE_URL: 'http://localhost:8080/api',
        elements: {
            // Producto Modal Elements
            btnNuevoProducto: document.getElementById('btn-nuevo-producto'),
            modalProducto: new bootstrap.Modal(document.getElementById('modalProducto')),
            formProducto: document.getElementById('formProducto'),
            productoId: document.getElementById('producto-id'),
            productoNombre: document.getElementById('producto-nombre'),
            productoDescripcion: document.getElementById('producto-descripcion'),
            productoStock: document.getElementById('producto-stock'),
            productoPrecio: document.getElementById('producto-precio'),
            productoFechaCaducidad: document.getElementById('producto-fecha-caducidad'),
            productoIgv: document.getElementById('producto-igv'),
            productoPrecioFinal: document.getElementById('producto-precio-final'),
            productoImagenFile: document.getElementById('producto-imagen-file'), // Input de tipo file
            productoImagenUrl: document.getElementById('producto-imagen-url'),   // Campo oculto para la URL final
            imagenPreview: document.getElementById('imagen-preview'),           // Contenedor para la vista previa
            guardarProductoBtn: document.getElementById('guardar-producto-btn'),

            // Filtros de Productos
            filtroNombre: document.getElementById('filtro-nombre'),
            filtroStockMin: document.getElementById('filtro-stock-min'),
            filtroStockMax: document.getElementById('filtro-stock-max'),
            tablaProductosBody: document.getElementById('tabla-productos-body'),
            btnAplicarFiltros: document.getElementById('btn-aplicar-filtros'),

            // Repartidor Modal Elements (Ajusta si no los usas)
            btnNuevoRepartidor: document.getElementById('btn-nuevo-repartidor'),
            modalRepartidor: new bootstrap.Modal(document.getElementById('modalRepartidor')),
            formRepartidor: document.getElementById('formRepartidor'),
            repartidorId: document.getElementById('repartidor-id'),
            repartidorNombre: document.getElementById('repartidor-nombre'),
            repartidorLicencia: document.getElementById('repartidor-licencia'),
            repartidorTelefono: document.getElementById('repartidor-telefono'),
            repartidorDisponibilidad: document.getElementById('repartidor-disponibilidad'),
            repartidorContrasena: document.getElementById('repartidor-contrasena'),
            repartidorContrasenaGroup: document.getElementById('repartidor-contrasena-group'),
            guardarRepartidorBtn: document.getElementById('guardar-repartidor-btn'),

            // Filtros de Repartidores
            filtroRepartidorNombre: document.getElementById('filtro-repartidor-nombre'),
            filtroRepartidorLicencia: document.getElementById('filtro-repartidor-licencia'),
            filtroRepartidorDisponibilidad: document.getElementById('filtro-repartidor-disponibilidad'),
            tablaRepartidoresBody: document.getElementById('tabla-repartidores-body'),
            btnAplicarFiltrosRepartidor: document.getElementById('btn-aplicar-filtros-repartidor')
        },

        state: {
            productos: [],
            repartidores: [],
            selectedProductId: null,
            selectedRepartidorId: null
        },

        init: function() {
            this.setupEventListeners();
            this.fetchProducts();
            this.fetchRepartidores();
            this.calcularPrecioFinal(); // Calcula el precio final inicial al cargar
        },

        setupEventListeners: function() {
            // Product Event Listeners
            this.elements.btnNuevoProducto.addEventListener('click', () => this.openProductModal());
            this.elements.guardarProductoBtn.addEventListener('click', () => this.saveProduct());
            this.elements.productoPrecio.addEventListener('input', () => this.calcularPrecioFinal());
            this.elements.productoIgv.addEventListener('input', () => this.calcularPrecioFinal());
            this.elements.productoImagenFile.addEventListener('change', (event) => this.previewImage(event));
            this.elements.btnAplicarFiltros.addEventListener('click', () => this.aplicarFiltros());
            this.elements.filtroNombre.addEventListener('input', () => this.aplicarFiltros());
            this.elements.filtroStockMin.addEventListener('input', () => this.aplicarFiltros());
            this.elements.filtroStockMax.addEventListener('input', () => this.aplicarFiltros());

            // Repartidor Event Listeners
            this.elements.btnNuevoRepartidor.addEventListener('click', () => this.openRepartidorModal());
            this.elements.guardarRepartidorBtn.addEventListener('click', () => this.saveRepartidor());
            this.elements.btnAplicarFiltrosRepartidor.addEventListener('click', () => this.aplicarFiltrosRepartidor());
            this.elements.filtroRepartidorNombre.addEventListener('input', () => this.aplicarFiltrosRepartidor());
            this.elements.filtroRepartidorLicencia.addEventListener('input', () => this.aplicarFiltrosRepartidor());
            this.elements.filtroRepartidorDisponibilidad.addEventListener('change', () => this.aplicarFiltrosRepartidor());
        },

        // --- Funciones de Productos ---

        calcularPrecioFinal: function() {
            const precioSinIgv = parseFloat(this.elements.productoPrecio.value) || 0;
            const igvPorcentaje = parseFloat(this.elements.productoIgv.value) || 0;
            const precioFinal = precioSinIgv * (1 + igvPorcentaje);
            this.elements.productoPrecioFinal.value = precioFinal.toFixed(2);
        },

        previewImage: function(event) {
            const [file] = event.target.files;
            if (file) {
                this.elements.imagenPreview.innerHTML = `<img src="${URL.createObjectURL(file)}" class="img-thumbnail" style="max-width: 100px; max-height: 100px; object-fit: cover;">`;
            } else {
                this.elements.imagenPreview.innerHTML = '';
            }
        },

        openProductModal: function(productId = null) {
            this.elements.formProducto.reset();
            this.state.selectedProductId = productId;
            this.elements.productoId.value = '';
            // Asegúrate que 'repartidorContrasenaGroup' exista si se usa en otros contextos o elimínalo si no aplica aquí
            if (this.elements.repartidorContrasenaGroup) {
                this.elements.repartidorContrasenaGroup.style.display = 'block';
            }
            this.elements.imagenPreview.innerHTML = '';
            this.elements.productoImagenUrl.value = '';

            if (productId) {
                const product = this.state.productos.find(p => p.idProducto === productId);
                if (product) {
                    this.elements.productoId.value = product.idProducto;
                    this.elements.productoNombre.value = product.nombre;
                    this.elements.productoDescripcion.value = product.descripcion;
                    this.elements.productoStock.value = product.stockDisponible;
                    this.elements.productoPrecio.value = product.precio;
                    this.elements.productoFechaCaducidad.value = product.fechaCaducidad || '';
                    this.elements.productoIgv.value = product.igv;
                    this.elements.productoPrecioFinal.value = product.precioFinal;
                    this.elements.productoImagenUrl.value = product.imagen || '';
                    if (product.imagen) {
                        this.elements.imagenPreview.innerHTML = `<img src="${product.imagen}" class="img-thumbnail" style="max-width: 100px; max-height: 100px; object-fit: cover;">`;
                    }
                    if (this.elements.repartidorContrasenaGroup) {
                        this.elements.repartidorContrasenaGroup.style.display = 'none';
                    }
                }
                this.elements.modalProducto.show();
            } else {
                this.elements.modalProducto.show();
                this.calcularPrecioFinal();
            }
        },

        uploadImage: async function(file) {
            const formData = new FormData();
            formData.append('file', file);

            try {
                // Este endpoint debe ser manejado por tu backend para guardar el archivo.
                // Ejemplo: http://localhost:8080/api/upload-image
                const response = await fetch(`${this.API_BASE_URL}/upload-image`, {
                    method: 'POST',
                    body: formData
                });

                if (!response.ok) {
                    const errorText = await response.text();
                    throw new Error(`Error al subir imagen: ${response.statusText} - ${errorText}`);
                }

                const result = await response.json();
                // Asume que tu backend devuelve un JSON con la ruta relativa del archivo guardado.
                // Ejemplo: { filePath: "../images/P/nombre_imagen.webp" }
                return result.filePath;
            } catch (error) {
                console.error('Error en uploadImage:', error);
                throw error;
            }
        },

        saveProduct: async function() {
            const productId = this.elements.productoId.value;
            const isEditing = !!productId;
            const imageFile = this.elements.productoImagenFile.files[0];
            let imageUrlToSave = this.elements.productoImagenUrl.value;

            try {
                if (imageFile) {
                    imageUrlToSave = await this.uploadImage(imageFile);
                }

                const productData = {
                    nombre: this.elements.productoNombre.value,
                    descripcion: this.elements.productoDescripcion.value,
                    stockDisponible: parseInt(this.elements.productoStock.value),
                    precio: parseFloat(this.elements.productoPrecio.value),
                    fechaCaducidad: this.elements.productoFechaCaducidad.value || null,
                    igv: parseFloat(this.elements.productoIgv.value),
                    precioFinal: parseFloat(this.elements.productoPrecioFinal.value),
                    imagen: imageUrlToSave,
                    fechaIngreso: new Date().toISOString().slice(0, 10)
                };

                if (isEditing) {
                    productData.idProducto = parseInt(productId);
                }

                const url = isEditing ? `${this.API_BASE_URL}/productos/${productId}` : `${this.API_BASE_URL}/productos`;
                const method = isEditing ? 'PUT' : 'POST';

                const response = await fetch(url, {
                    method: method,
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify(productData)
                });

                if (!response.ok) {
                    const errorData = await response.json();
                    throw new Error(`Error al guardar producto: ${errorData.message || response.statusText}`);
                }

                this.elements.modalProducto.hide();
                await this.fetchProducts();
                this.aplicarFiltros();
                alert('Producto guardado exitosamente.');
            } catch (error) {
                console.error('Error al guardar producto:', error);
                alert('Error al guardar producto: ' + error.message);
            }
        },

        fetchProducts: async function() {
            try {
                const response = await fetch(`${this.API_BASE_URL}/productos`);
                if (!response.ok) {
                    throw new Error('Error al obtener productos');
                }
                this.state.productos = await response.json();
                this.renderProducts(this.state.productos);
            } catch (error) {
                console.error('Error fetching products:', error);
                alert('Error al cargar los productos.');
            }
        },

        renderProducts: function(productsToRender) {
            this.elements.tablaProductosBody.innerHTML = '';
            if (productsToRender.length === 0) {
                this.elements.tablaProductosBody.innerHTML = `<tr><td colspan="9" class="text-center">No hay productos disponibles o que coincidan con el filtro.</td></tr>`;
                return;
            }
            productsToRender.forEach(product => {
                const row = document.createElement('tr');
                row.innerHTML = `
                    <td>${product.idProducto}</td>
                    <td><img src="${product.imagen}" alt="${product.nombre}" style="width: 50px; height: 50px; object-fit: cover;"></td>
                    <td>${product.nombre}</td>
                    <td>${product.descripcion}</td>
                    <td>${product.stockDisponible}</td>
                    <td>S/ ${product.precio.toFixed(2)}</td>
                    <td>S/ ${product.precioFinal.toFixed(2)}</td>
                    <td>${product.fechaCaducidad || 'N/A'}</td>
                    <td>
                        <button class="btn btn-sm btn-info editar-producto-btn" data-id="${product.idProducto}"><i class="fas fa-edit"></i></button>
                        <button class="btn btn-sm btn-danger eliminar-producto-btn" data-id="${product.idProducto}"><i class="fas fa-trash"></i></button>
                    </td>
                `;
                this.elements.tablaProductosBody.appendChild(row);
            });
            this.elements.tablaProductosBody.querySelectorAll('.editar-producto-btn').forEach(button => {
                button.addEventListener('click', (e) => this.openProductModal(parseInt(e.currentTarget.dataset.id)));
            });
            this.elements.tablaProductosBody.querySelectorAll('.eliminar-producto-btn').forEach(button => {
                button.addEventListener('click', (e) => this.deleteProduct(parseInt(e.currentTarget.dataset.id)));
            });
        },

        aplicarFiltros: function() {
            const nombreFiltro = this.elements.filtroNombre.value.toLowerCase();
            const stockMinFiltro = parseInt(this.elements.filtroStockMin.value) || 0;
            const stockMaxFiltro = parseInt(this.elements.filtroStockMax.value) || Infinity;

            const productosFiltrados = this.state.productos.filter(product => {
                const nombreCoincide = product.nombre.toLowerCase().includes(nombreFiltro);
                const stockCoincide = product.stockDisponible >= stockMinFiltro && product.stockDisponible <= stockMaxFiltro;
                return nombreCoincide && stockCoincide;
            });
            this.renderProducts(productosFiltrados);
        },

        deleteProduct: async function(productId) {
            if (!confirm('¿Estás seguro de que quieres eliminar este producto?')) {
                return;
            }
            try {
                const response = await fetch(`${this.API_BASE_URL}/productos/${productId}`, {
                    method: 'DELETE'
                });

                if (!response.ok) {
                    const errorData = await response.json();
                    throw new Error(`Error al eliminar producto: ${errorData.message || response.statusText}`);
                }
                await this.fetchProducts();
                this.aplicarFiltros();
                alert('Producto eliminado exitosamente.');
            } catch (error) {
                console.error('Error deleting product:', error);
                alert('Error al eliminar producto: ' + error.message);
            }
        },

        // --- Funciones de Repartidores ---

        openRepartidorModal: function(repartidorId = null) {
            this.elements.formRepartidor.reset();
            this.state.selectedRepartidorId = repartidorId;
            this.elements.repartidorId.value = '';
            this.elements.repartidorContrasenaGroup.style.display = 'block';

            if (repartidorId) {
                const repartidor = this.state.repartidores.find(r => r.idRepartidor === repartidorId);
                if (repartidor) {
                    this.elements.repartidorId.value = repartidor.idRepartidor;
                    this.elements.repartidorNombre.value = repartidor.nombre;
                    this.elements.repartidorLicencia.value = repartidor.licencia;
                    this.elements.repartidorTelefono.value = repartidor.telefono;
                    this.elements.repartidorDisponibilidad.value = repartidor.disponibilidad ? 'true' : 'false';
                    this.elements.repartidorContrasena.value = ''; // No cargar contraseña existente por seguridad
                    this.elements.repartidorContrasenaGroup.style.display = 'none'; // Ocultar campo de contraseña en edición
                }
            }
            this.elements.modalRepartidor.show();
        },

        saveRepartidor: async function() {
            const repartidorId = this.elements.repartidorId.value;
            const isEditing = !!repartidorId;

            const repartidorData = {
                nombre: this.elements.repartidorNombre.value,
                licencia: this.elements.repartidorLicencia.value,
                telefono: this.elements.repartidorTelefono.value,
                disponibilidad: this.elements.repartidorDisponibilidad.value === 'true'
            };

            // Solo añadir contraseña si es un nuevo repartidor
            if (!isEditing && this.elements.repartidorContrasena.value) {
                repartidorData.contrasena = this.elements.repartidorContrasena.value;
            }

            try {
                const url = isEditing ? `${this.API_BASE_URL}/repartidores/${repartidorId}` : `${this.API_BASE_URL}/repartidores`;
                const method = isEditing ? 'PUT' : 'POST';

                const response = await fetch(url, {
                    method: method,
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify(repartidorData)
                });

                if (!response.ok) {
                    const errorData = await response.json();
                    throw new Error(`Error al guardar repartidor: ${errorData.message || response.statusText}`);
                }

                this.elements.modalRepartidor.hide();
                await this.fetchRepartidores();
                this.aplicarFiltrosRepartidor();
                alert('Repartidor guardado exitosamente.');
            } catch (error) {
                console.error('Error al guardar repartidor:', error);
                alert('Error al guardar repartidor: ' + error.message);
            }
        },

        fetchRepartidores: async function() {
            try {
                const response = await fetch(`${this.API_BASE_URL}/repartidores`);
                if (!response.ok) {
                    throw new Error('Error al obtener repartidores');
                }
                this.state.repartidores = await response.json();
                this.renderRepartidores(this.state.repartidores);
            } catch (error) {
                console.error('Error fetching repartidores:', error);
                alert('Error al cargar los repartidores.');
            }
        },

        renderRepartidores: function(repartidoresToRender) {
            this.elements.tablaRepartidoresBody.innerHTML = '';
            if (repartidoresToRender.length === 0) {
                this.elements.tablaRepartidoresBody.innerHTML = `<tr><td colspan="6" class="text-center">No hay repartidores disponibles o que coincidan con el filtro.</td></tr>`;
                return;
            }
            repartidoresToRender.forEach(repartidor => {
                const row = document.createElement('tr');
                row.innerHTML = `
                    <td>${repartidor.idRepartidor}</td>
                    <td>${repartidor.nombre}</td>
                    <td>${repartidor.licencia}</td>
                    <td>${repartidor.telefono}</td>
                    <td>${repartidor.disponibilidad ? '<span class="badge bg-success">Disponible</span>' : '<span class="badge bg-danger">No Disponible</span>'}</td>
                    <td>
                        <button class="btn btn-sm btn-info editar-repartidor-btn" data-id="${repartidor.idRepartidor}"><i class="fas fa-edit"></i></button>
                        <button class="btn btn-sm btn-danger eliminar-repartidor-btn" data-id="${repartidor.idRepartidor}"><i class="fas fa-trash"></i></button>
                    </td>
                `;
                this.elements.tablaRepartidoresBody.appendChild(row);
            });
            this.elements.tablaRepartidoresBody.querySelectorAll('.editar-repartidor-btn').forEach(button => {
                button.addEventListener('click', (e) => this.openRepartidorModal(parseInt(e.currentTarget.dataset.id)));
            });
            this.elements.tablaRepartidoresBody.querySelectorAll('.eliminar-repartidor-btn').forEach(button => {
                button.addEventListener('click', (e) => this.deleteRepartidor(parseInt(e.currentTarget.dataset.id)));
            });
        },

        aplicarFiltrosRepartidor: function() {
            const nombreFiltro = this.elements.filtroRepartidorNombre.value.toLowerCase();
            const licenciaFiltro = this.elements.filtroRepartidorLicencia.value.toLowerCase();
            const disponibilidadFiltro = this.elements.filtroRepartidorDisponibilidad.value;

            const repartidoresFiltrados = this.state.repartidores.filter(repartidor => {
                const nombreCoincide = repartidor.nombre.toLowerCase().includes(nombreFiltro);
                const licenciaCoincide = repartidor.licencia.toLowerCase().includes(licenciaFiltro);
                const disponibilidadCoincide = disponibilidadFiltro === 'all' ||
                                               (disponibilidadFiltro === 'true' && repartidor.disponibilidad) ||
                                               (disponibilidadFiltro === 'false' && !repartidor.disponibilidad);
                return nombreCoincide && licenciaCoincide && disponibilidadCoincide;
            });
            this.renderRepartidores(repartidoresFiltrados);
        },

        deleteRepartidor: async function(repartidorId) {
            if (!confirm('¿Estás seguro de que quieres eliminar este repartidor?')) {
                return;
            }
            try {
                const response = await fetch(`${this.API_BASE_URL}/repartidores/${repartidorId}`, {
                    method: 'DELETE'
                });

                if (!response.ok) {
                    const errorData = await response.json();
                    throw new Error(`Error al eliminar repartidor: ${errorData.message || response.statusText}`);
                }
                await this.fetchRepartidores();
                this.aplicarFiltrosRepartidor();
                alert('Repartidor eliminado exitosamente.');
            } catch (error) {
                console.error('Error deleting repartidor:', error);
                alert('Error al eliminar repartidor: ' + error.message);
            }
        }
    };

    adminPanel.init();
});