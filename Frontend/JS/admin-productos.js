document.addEventListener("DOMContentLoaded", async () => {
    const API_BASE_URL = "http://localhost:8080/api";
    const API_PRODUCTOS_URL = `${API_BASE_URL}/productos`;
    const API_LOTES_URL = `${API_BASE_URL}/lotes-producto`;
    const API_UPLOAD_IMAGE_URL = `${API_BASE_URL}/upload-image`;

    const btnNuevoProducto = document.getElementById("btn-nuevo-producto");
    const clearFiltersBtn = document.getElementById("productos-clear-filters-btn");

    const filterNombre = document.getElementById("productos-filter-nombre");
    const filterCantidad = document.getElementById("productos-filter-cantidad");
    const filterCantidadOpcion = document.getElementById("productos-filter-cantidad-opcion");
    const filterIndicator = document.getElementById("productos-filter-indicator");

    const tablaLotesBody = document.getElementById("productos-table-body");

    let modalProductoBase = null;
    const modalProductoBodyContent = document.getElementById("modal-producto-body-content");
    const guardarProductoBaseBtn = document.getElementById("guardar-producto-btn");

    let modalLoteForm = null;
    const modalLoteBodyContent = document.getElementById("modal-lote-body-content");
    const loteModalProductName = document.getElementById("lote-modal-product-name");
    const guardarLoteBtn = document.getElementById("guardar-lote-btn");

    let allLotesDeProductos = []; // Almacenará TODOS los lotes sin filtrar
    let productosBase = [];
    let fileToUpload = null;

    const IGV_RATE = 0.18;

    if (document.getElementById("modal-producto")) {
        modalProductoBase = new bootstrap.Modal(document.getElementById("modal-producto"));
    }
    if (document.getElementById("modal-lote-form")) {
        modalLoteForm = new bootstrap.Modal(document.getElementById("modal-lote-form"));
    }

    if (btnNuevoProducto) {
        btnNuevoProducto.addEventListener("click", () => openProductBaseModal());
    }

    if (guardarProductoBaseBtn) {
        guardarProductoBaseBtn.addEventListener("click", () => saveProductBase());
    }

    if (clearFiltersBtn) {
        clearFiltersBtn.addEventListener("click", () => limpiarFiltrosLotes());
    }

    // Los eventos de input/change ahora llamarán a aplicarFiltrosLotes para filtrar en el frontend
    if (filterNombre) {
        filterNombre.addEventListener("input", () => aplicarFiltrosLotes());
    }
    if (filterCantidad) {
        filterCantidad.addEventListener("input", () => aplicarFiltrosLotes());
    }
    if (filterCantidadOpcion) {
        filterCantidadOpcion.addEventListener("change", () => aplicarFiltrosLotes());
    }

    if (guardarLoteBtn) {
        guardarLoteBtn.addEventListener("click", () => saveLote());
    }

    function previewImage(event, previewElementId) {
        const previewElement = document.getElementById(previewElementId);
        const [file] = event.target.files;
        if (file) {
            previewElement.innerHTML = `<img src="${URL.createObjectURL(file)}" class="img-thumbnail" style="max-width: 100px; max-height: 100px; object-fit: cover;">`;
        } else {
            previewElement.innerHTML = "";
        }
    }

    async function uploadImage(file) {
        const formData = new FormData();
        formData.append("file", file);

        try {
            const response = await fetch(API_UPLOAD_IMAGE_URL, {
                method: "POST",
                body: formData,
            });

            if (!response.ok) {
                const errorText = await response.text();
                throw new Error(`Error al subir imagen: ${errorText}`);
            }

            const data = await response.json();
            return data.filePath;
        } catch (error) {
            alert("Error al subir la imagen: " + error.message);
            return null;
        }
    }

    function calculatePrecioFinal(precioInputId, precioFinalInputId) {
        const precioInput = document.getElementById(precioInputId);
        const precioFinalInput = document.getElementById(precioFinalInputId);
        const precio = parseFloat(precioInput.value);
        if (!isNaN(precio) && precio >= 0) {
            precioFinalInput.value = (precio * (1 + IGV_RATE)).toFixed(2);
        } else {
            precioFinalInput.value = "";
        }
    }

    async function openProductBaseModal(productId = null) {
        modalProductoBodyContent.innerHTML = '<p class="text-center text-muted">Cargando formulario...</p>';

        let product = null;
        if (productId) {
            product = productosBase.find(p => p.idProducto === productId);
            if (!product) {
                alert(`Producto base con ID ${productId} no encontrado para editar.`);
                modalProductoBodyContent.innerHTML = '<p class="text-center text-danger">Error: Producto no encontrado.</p>';
                return;
            }
        }

        const isEditing = !!productId;
        const modalTitle = isEditing ? "Gestión de Producto Base" : "Gestión de Producto Base y Lote Inicial";
        const saveButtonText = isEditing ? "Guardar Cambios del Producto Base" : "Guardar Producto Base y Lote";

        document.getElementById("modal-producto-label").textContent = modalTitle;
        guardarProductoBaseBtn.textContent = saveButtonText;

        let initialImageUrl = product ? product.imagen : '';
        let initialPrecio = product ? product.precio : '';
        let initialPrecioFinal = product ? (product.precioFinal || (product.precio * (1 + IGV_RATE))).toFixed(2) : '';

        let formHtml = `
            <form id="form-producto">
                <input type="hidden" id="producto-id-input" value="${product ? product.idProducto : ''}">
                <div class="mb-3">
                    <label for="producto-nombre-input" class="form-label">Nombre del Producto</label>
                    <input type="text" class="form-control" id="producto-nombre-input" value="${product ? product.nombre : ''}" required>
                </div>
                <div class="mb-3">
                    <label for="producto-descripcion-input" class="form-label">Descripción</label>
                    <textarea class="form-control" id="producto-descripcion-input" rows="3">${product ? product.descripcion : ''}</textarea>
                </div>
                <div class="mb-3">
                    <label for="producto-laboratorio-input" class="form-label">Laboratorio</label>
                    <input type="text" class="form-control" id="producto-laboratorio-input" value="${product ? product.laboratorio : ''}">
                </div>
                <div class="mb-3">
                    <label for="producto-precio-input" class="form-label">Precio (sin IGV)</label>
                    <input type="number" class="form-control" id="producto-precio-input" step="0.01" min="0.01" value="${initialPrecio}" required>
                </div>
                <div class="mb-3">
                    <label for="producto-precio-final-input" class="form-label">Precio Final (con IGV)</label>
                    <input type="number" class="form-control" id="producto-precio-final-input" step="0.01" min="0.01" value="${initialPrecioFinal}" required readonly>
                </div>
                <div class="mb-3">
                    <label for="producto-imagen-file-input" class="form-label">Imagen del Producto</label>
                    <input type="file" class="form-control" id="producto-imagen-file-input" accept="image/*">
                    <input type="hidden" id="producto-imagen-url-input" value="${initialImageUrl}">
                    <small class="form-text text-muted">Selecciona un archivo de imagen.</small>
                    <div id="producto-imagen-preview" class="mt-2">
                        ${initialImageUrl ? `<img src="${initialImageUrl}" class="img-thumbnail" style="max-width: 100px; max-height: 100px; object-fit: cover;">` : ''}
                    </div>
                </div>
        `;

        if (!isEditing) {
            formHtml += `
                <hr>
                <h5>Datos del Lote Inicial</h5>
                <p class="text-muted"><small>Se creará un lote con este producto base.</small></p>
                <div class="mb-3">
                    <label for="lote-inicial-numero-input" class="form-label">Número de Lote Inicial</label>
                    <input type="text" class="form-control" id="lote-inicial-numero-input" required>
                </div>
                <div class="mb-3">
                    <label for="lote-inicial-cantidad-input" class="form-label">Cantidad Inicial en Lote</label>
                    <input type="number" class="form-control" id="lote-inicial-cantidad-input" min="1" required>
                </div>
                <div class="mb-3">
                    <label for="lote-inicial-fecha-caducidad-input" class="form-label">Fecha de Caducidad (Lote Inicial - Opcional)</label>
                    <input type="date" class="form-control" id="lote-inicial-fecha-caducidad-input">
                </div>
            `;
        }

        formHtml += `</form>`;
        modalProductoBodyContent.innerHTML = formHtml;

        const productoPrecioBase = document.getElementById("producto-precio-input");
        const productoImagenFileBase = document.getElementById("producto-imagen-file-input");

        if (productoPrecioBase) {
            productoPrecioBase.addEventListener("input", () => calculatePrecioFinal("producto-precio-input", "producto-precio-final-input"));
        }
        if (productoImagenFileBase) {
            productoImagenFileBase.addEventListener("change", (event) => {
                const [file] = event.target.files;
                if (file) {
                    previewImage(event, "producto-imagen-preview");
                    fileToUpload = file;
                } else {
                    document.getElementById("producto-imagen-preview").innerHTML = "";
                    fileToUpload = null;
                    document.getElementById("producto-imagen-url-input").value = "";
                }
            });
        }

        if (modalProductoBase) {
            modalProductoBase.show();
        }
    }

    async function saveProductBase() {
        const productoIdBase = document.getElementById("producto-id-input");
        const productoNombreBase = document.getElementById("producto-nombre-input");
        const productoDescripcionBase = document.getElementById("producto-descripcion-input");
        const productoLaboratorioBase = document.getElementById("producto-laboratorio-input");
        const productoPrecioBase = document.getElementById("producto-precio-input");
        const productoPrecioFinalBase = document.getElementById("producto-precio-final-input");
        const productoImagenUrlBase = document.getElementById("producto-imagen-url-input");

        const loteInicialNumeroInput = document.getElementById("lote-inicial-numero-input");
        const loteInicialCantidadInput = document.getElementById("lote-inicial-cantidad-input");
        const loteInicialFechaCaducidadInput = document.getElementById("lote-inicial-fecha-caducidad-input");

        const productId = productoIdBase.value;
        const isEditing = !!productId;
        let finalImageUrl = productoImagenUrlBase.value;

        if (!productoNombreBase.value.trim()) {
            alert("El nombre del producto es obligatorio.");
            return;
        }
        const precio = parseFloat(productoPrecioBase.value);
        if (isNaN(precio) || precio <= 0) {
            alert("El precio debe ser un número positivo.");
            return;
        }

        if (!isEditing) {
            if (!loteInicialNumeroInput || !loteInicialNumeroInput.value.trim()) {
                alert("El número de lote inicial es obligatorio.");
                return;
            }
            const cantidad = parseInt(loteInicialCantidadInput.value);
            if (isNaN(cantidad) || cantidad <= 0) {
                alert("La cantidad inicial del lote debe ser un número entero positivo.");
                return;
            }
        }

        if (fileToUpload) {
            try {
                const uploadedUrl = await uploadImage(fileToUpload);
                if (uploadedUrl) {
                    finalImageUrl = uploadedUrl;
                } else {
                    alert("No se pudo subir la nueva imagen. Abortando guardar producto.");
                    return;
                }
            } catch (uploadError) {
                alert("Error crítico al subir la imagen: " + uploadError.message);
                return;
            }
        }

        try {
            const productData = {
                nombre: productoNombreBase.value,
                descripcion: productoDescripcionBase.value,
                laboratorio: productoLaboratorioBase.value,
                precio: precio,
                imagen: finalImageUrl,
                igv: IGV_RATE,
                precioFinal: Number.parseFloat(productoPrecioFinalBase.value),
            };

            if (isEditing) {
                productData.idProducto = Number.parseInt(productId);
            }

            const productUrl = isEditing ? `${API_PRODUCTOS_URL}/${productId}` : API_PRODUCTOS_URL;
            const productMethod = isEditing ? "PUT" : "POST";

            const productResponse = await fetch(productUrl, {
                method: productMethod,
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(productData),
            });

            if (!productResponse.ok) {
                const errorText = await productResponse.text();
                throw new Error(`Error al guardar producto base: ${errorText}`);
            }

            const savedProduct = await productResponse.json();

            if (!isEditing) {
                const loteData = {
                    idProducto: savedProduct.idProducto,
                    numeroLote: loteInicialNumeroInput.value,
                    cantidadDisponible: Number.parseInt(loteInicialCantidadInput.value),
                    fechaCaducidad: loteInicialFechaCaducidadInput ? loteInicialFechaCaducidadInput.value : null,
                };

                const loteResponse = await fetch(API_LOTES_URL, {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify(loteData),
                });

                if (!loteResponse.ok) {
                    const errorText = await loteResponse.text();
                    alert("Producto base guardado, pero hubo un error al crear el lote inicial: " + errorText);
                }
            }

            if (modalProductoBase) modalProductoBase.hide();
            alert("Producto base y lote (si aplica) guardado/s exitosamente.");
            fileToUpload = null;

            await fetchAllProductBases();
            await fetchAllLotes(); // Vuelve a cargar TODOS los lotes
        } catch (error) {
            alert("Error al guardar producto base o lote: " + error.message);
            console.error("Error en saveProductBase:", error);
        }
    }

    async function deleteProductBase(productId) {
        if (!confirm("¿Estás seguro de que quieres eliminar este producto BASE? Esto eliminará el producto y TODOS sus lotes asociados.")) {
            return;
        }

        try {
            const response = await fetch(`${API_PRODUCTOS_URL}/${productId}`, { method: "DELETE" });

            if (!response.ok) {
                throw new Error("Error al eliminar producto base");
            }

            alert("Producto base eliminado exitosamente.");
            await fetchAllProductBases();
            await fetchAllLotes(); // Vuelve a cargar TODOS los lotes
        } catch (error) {
            alert("Error al eliminar producto base: " + error.message);
            console.error("Error en deleteProductBase:", error);
        }
    }

    async function deleteLote(loteId) {
        if (!confirm("¿Estás seguro de que quieres eliminar este lote? Esta acción no se puede deshacer.")) {
            return;
        }

        try {
            const response = await fetch(`${API_LOTES_URL}/${loteId}`, { method: "DELETE" });

            if (!response.ok) {
                const errorText = await response.text();
                throw new Error(`Error al eliminar lote: ${errorText}`);
            }

            alert("Lote eliminado exitosamente.");
            await fetchAllLotes(); // Vuelve a cargar TODOS los lotes
        } catch (error) {
            alert("Error al eliminar lote: " + error.message);
            console.error("Error en deleteLote:", error);
        }
    }

    // fetchAllLotes ahora solo obtiene todos los lotes
    async function fetchAllLotes() {
        try {
            const response = await fetch(API_LOTES_URL); // SIN PARÁMETROS DE FILTRO
            if (!response.ok) {
                if (response.status === 204 || response.status === 404) {
                    allLotesDeProductos = [];
                    renderLotesTable(); // Llama a render sin filtros específicos
                    return;
                }
                throw new Error(`Error al obtener lotes: HTTP status ${response.status}`);
            }
            allLotesDeProductos = await response.json();
            aplicarFiltrosLotes(); // Aplica los filtros después de obtener todos los lotes
        } catch (error) {
            console.error("Error al obtener lotes:", error);
            tablaLotesBody.innerHTML =
                '<tr><td colspan="12" class="text-center py-4 text-danger">Error al cargar los lotes de productos.</td></tr>';
        }
    }

    // renderLotesTable ahora recibe los lotes YA FILTRADOS
    function renderLotesTable(lotesToRender) {
        tablaLotesBody.innerHTML = "";

        if (lotesToRender.length === 0) {
            tablaLotesBody.innerHTML =
                '<tr><td colspan="12" class="text-center py-4">No hay lotes de productos disponibles con los filtros aplicados.</td></tr>';
            return;
        }

        lotesToRender.forEach((lote) => {
            const product = productosBase.find(p => p.idProducto === lote.idProducto);
            const row = document.createElement("tr");
            row.innerHTML = `
                <td>${lote.idLoteProducto}</td>
                <td>${lote.numeroLote}</td>
                <td>${product ? product.nombre : 'N/A'}</td>
                <td>${product ? product.descripcion : 'N/A'}</td>
                <td>${product ? product.laboratorio : 'N/A'}</td>
                <td>${lote.cantidadDisponible}</td>
                <td>$${(product ? product.precio : 0).toFixed(2)}</td>
                <td>$${(product ? (product.precioFinal || (product.precio * (1 + IGV_RATE))) : 0).toFixed(2)}</td>
                <td>${lote.fechaCaducidad || "N/A"}</td>
                <td>${lote.fechaIngreso}</td>
                <td><img src="${product && product.imagen ? product.imagen : '../images/placeholder.png'}" alt="${product ? product.nombre : 'Producto'}" style="width: 50px; height: 50px; object-fit: cover; border-radius: 4px;"></td>
                <td>
                    <button class="btn btn-sm btn-info edit-product-base-btn" data-id="${product ? product.idProducto : ''}"><i class="fas fa-box"></i> Editar Base</button>
                    <button class="btn btn-sm btn-warning edit-lote-btn" data-id="${lote.idLoteProducto}"><i class="fas fa-edit"></i> Editar Lote</button>
                    <button class="btn btn-sm btn-success add-lote-btn" data-product-id="${product ? product.idProducto : ''}" data-product-name="${product ? product.nombre : ''}"><i class="fas fa-plus"></i> Nuevo Lote</button>
                    <button class="btn btn-sm btn-danger delete-lote-btn" data-id="${lote.idLoteProducto}"><i class="fas fa-trash-alt"></i> Eliminar Lote</button>
                </td>
            `;
            tablaLotesBody.appendChild(row);
        });

        tablaLotesBody.querySelectorAll(".edit-product-base-btn").forEach((button) => {
            button.addEventListener("click", (e) => openProductBaseModal(Number.parseInt(e.currentTarget.dataset.id)));
        });

        tablaLotesBody.querySelectorAll(".edit-lote-btn").forEach((button) => {
            button.addEventListener("click", (e) => openLoteModal(Number.parseInt(e.currentTarget.dataset.id)));
        });

        tablaLotesBody.querySelectorAll(".add-lote-btn").forEach((button) => {
            button.addEventListener("click", (e) => {
                const productId = Number.parseInt(e.currentTarget.dataset.productId);
                const productName = e.currentTarget.dataset.productName;
                openLoteModal(null, productId, productName);
            });
        });

        tablaLotesBody.querySelectorAll(".delete-lote-btn").forEach((button) => {
            button.addEventListener("click", (e) => deleteLote(Number.parseInt(e.currentTarget.dataset.id)));
        });
    }

    // Esta función ahora realiza el filtrado en el array allLotesDeProductos
    function aplicarFiltrosLotes() {
        let lotesFiltrados = [...allLotesDeProductos]; // Trabaja con una copia
        let indicadorTexto = "";

        const nombreFiltro = filterNombre.value.trim().toLowerCase();
        const cantidadFiltro = parseFloat(filterCantidad.value);
        const cantidadOpcionFiltro = filterCantidadOpcion.value;

        if (nombreFiltro !== "") {
            lotesFiltrados = lotesFiltrados.filter(lote => {
                const product = productosBase.find(p => p.idProducto === lote.idProducto);
                return product && product.nombre.toLowerCase().includes(nombreFiltro);
            });
            indicadorTexto += `Nombre Producto: "${nombreFiltro}" `;
        }

        if (!isNaN(cantidadFiltro)) {
            lotesFiltrados = lotesFiltrados.filter(lote => {
                if (cantidadOpcionFiltro === "mayor") {
                    return lote.cantidadDisponible > cantidadFiltro;
                } else if (cantidadOpcionFiltro === "menor") {
                    return lote.cantidadDisponible < cantidadFiltro;
                }
                return true; // Si no hay opción, no filtra por cantidad
            });
            indicadorTexto += `Cantidad ${cantidadOpcionFiltro === "mayor" ? ">" : "<"} ${cantidadFiltro} `;
        }

        if (indicadorTexto === "") {
            filterIndicator.textContent = "Sin filtros aplicados";
        } else {
            filterIndicator.textContent = `Filtros: ${indicadorTexto.trim()}`;
        }

        renderLotesTable(lotesFiltrados); // Renderiza los lotes ya filtrados
    }

    function limpiarFiltrosLotes() {
        filterNombre.value = "";
        filterCantidad.value = "";
        filterCantidadOpcion.value = "mayor";
        filterIndicator.textContent = "Sin filtros aplicados";
        aplicarFiltrosLotes(); // Llama a aplicarFiltrosLotes para renderizar sin filtros
    }

    async function openLoteModal(loteId = null, productIdFromTable = null, productNameFromTable = null) {
        modalLoteBodyContent.innerHTML = '<p class="text-center text-muted">Cargando formulario...</p>';

        let lote = null;
        if (loteId) {
            try {
                // Si el filtrado es en el frontend, y queremos editar un lote,
                // aún necesitamos obtener los detalles específicos del lote del backend si no los tenemos completos.
                // Para simplificar, asumimos que `allLotesDeProductos` contiene todos los detalles necesarios.
                lote = allLotesDeProductos.find(l => l.idLoteProducto === loteId);
                if (!lote) throw new Error("Lote no encontrado en el cache local.");
            } catch (error) {
                alert("Error al cargar datos del lote: " + error.message);
                modalLoteBodyContent.innerHTML = '<p class="text-center text-danger">Error: Lote no encontrado.</p>';
                return;
            }
        }

        const isEditing = !!loteId;
        let associatedProductId = productIdFromTable || (lote ? lote.idProducto : '');
        let associatedProductName = productNameFromTable || '';

        if (lote && !productNameFromTable) {
            const productBase = productosBase.find(p => p.idProducto === lote.idProducto);
            associatedProductName = productBase ? productBase.nombre : 'Producto Desconocido';
        }

        const modalTitle = isEditing ? `Editar Lote para ${associatedProductName}` : `Añadir Nuevo Lote para ${associatedProductName}`;
        const saveButtonText = isEditing ? "Guardar Cambios del Lote" : "Guardar Nuevo Lote";

        document.getElementById("modal-lote-form-label").textContent = modalTitle;
        loteModalProductName.textContent = associatedProductName;
        guardarLoteBtn.textContent = saveButtonText;

        const formLoteHtml = `
            <form id="form-lote-producto">
                <input type="hidden" id="lote-id-input" value="${lote ? lote.idLoteProducto : ''}">
                <input type="hidden" id="lote-producto-id-asociado" value="${associatedProductId}">
                <div class="mb-3">
                    <label for="lote-numero-input" class="form-label">Número de Lote</label>
                    <input type="text" class="form-control" id="lote-numero-input" value="${lote ? lote.numeroLote : ''}" required>
                </div>
                <div class="mb-3">
                    <label for="lote-cantidad-input" class="form-label">Cantidad Disponible</label>
                    <input type="number" class="form-control" id="lote-cantidad-input" min="1" value="${lote ? lote.cantidadDisponible : ''}" required>
                </div>
                <div class="mb-3">
                    <label for="lote-fecha-caducidad-input" class="form-label">Fecha de Caducidad (Opcional)</label>
                    <input type="date" class="form-control" id="lote-fecha-caducidad-input" value="${lote ? lote.fechaCaducidad : ''}">
                </div>
            </form>
        `;
        modalLoteBodyContent.innerHTML = formLoteHtml;

        if (modalLoteForm) modalLoteForm.show();
    }

    async function saveLote() {
        const loteIdInput = document.getElementById("lote-id-input");
        const loteProductoIdAsociado = document.getElementById("lote-producto-id-asociado");
        const loteNumeroInput = document.getElementById("lote-numero-input");
        const loteCantidadInput = document.getElementById("lote-cantidad-input");
        const loteFechaCaducidadInput = document.getElementById("lote-fecha-caducidad-input");

        const loteId = loteIdInput.value;
        const productId = loteProductoIdAsociado.value;
        const isEditing = !!loteId;

        if (!productId) {
            alert("Debe seleccionar un producto base para el lote.");
            return;
        }
        if (!loteNumeroInput.value.trim()) {
            alert("El número de lote es obligatorio.");
            return;
        }
        if (isNaN(parseInt(loteCantidadInput.value)) || parseInt(loteCantidadInput.value) <= 0) {
            alert("La cantidad debe ser un número entero positivo.");
            return;
        }

        try {
            const loteData = {
                idProducto: Number.parseInt(productId),
                numeroLote: loteNumeroInput.value,
                cantidadDisponible: Number.parseInt(loteCantidadInput.value),
                fechaCaducidad: loteFechaCaducidadInput.value || null,
            };

            if (isEditing) {
                loteData.idLoteProducto = Number.parseInt(loteId);
            }

            const url = isEditing ? `${API_LOTES_URL}/${loteId}` : API_LOTES_URL;
            const method = isEditing ? "PUT" : "POST";

            const response = await fetch(url, {
                method: method,
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(loteData),
            });

            if (!response.ok) {
                const errorText = await response.text();
                throw new Error(`Error al guardar lote: ${errorText}`);
            }

            if (modalLoteForm) modalLoteForm.hide();
            alert("Lote guardado exitosamente.");
            await fetchAllLotes(); // Vuelve a cargar TODOS los lotes
        } catch (error) {
            alert("Error al guardar lote: " + error.message);
            console.error("Error en saveLote:", error);
        }
    }

    async function fetchAllProductBases() {
        try {
            const response = await fetch(API_PRODUCTOS_URL);
            if (!response.ok) {
                if (response.status === 204 || response.status === 404) {
                    productosBase = [];
                    return;
                }
                throw new Error(`Error al obtener productos base: HTTP status ${response.status}`);
            }
            productosBase = await response.json();
        } catch (error) {
            console.error("Error al obtener productos base:", error);
        }
    }

    await fetchAllProductBases();
    await fetchAllLotes(); // Carga inicial de TODOS los lotes

    checkUrlFilters();

    function checkUrlFilters() {
        const urlParams = new URLSearchParams(window.location.search);
        const filter = urlParams.get("filter");

        if (filter) {
            switch (filter) {
                case "critico":
                    filterCantidadOpcion.value = "menor";
                    filterCantidad.value = "10";
                    break;
                case "bajo":
                    filterCantidadOpcion.value = "menor";
                    filterCantidad.value = "50";
                    break;
            }
            aplicarFiltrosLotes(); // Aplica los filtros desde la URL
        }
    }

    const toggleSidebarBtn = document.getElementById("toggle-sidebar-btn");
    const adminSidebar = document.getElementById("admin-sidebar");
    const mainContent = document.querySelector(".main-content");

    if (toggleSidebarBtn) {
        toggleSidebarBtn.addEventListener("click", () => {
            adminSidebar.classList.toggle("collapsed");
            mainContent.classList.toggle("collapsed");
        });
    }
});