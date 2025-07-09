document.addEventListener("DOMContentLoaded", () => {
  const API_BASE_URL = "http://localhost:8080/api";

  const btnNuevoRepartidor = document.getElementById("btn-nuevo-repartidor");
  const btnAplicarFiltros = document.getElementById("repartidores-apply-filters-btn");
  const btnClearFiltros = document.getElementById("repartidores-clear-filters-btn");
  const guardarRepartidorBtn = document.getElementById("guardar-repartidor-btn");
  const guardarContrasenaBtn = document.getElementById("guardar-contrasena-btn");

  const filtroNombre = document.getElementById("repartidores-filtro-nombre");

  const tablaRepartidoresBody = document.getElementById("repartidores-table-body");

  const modalRepartidorElement = document.getElementById("modal-repartidor");
  let modalRepartidorInstance = null; // Inicializar a null

  const formRepartidor = document.getElementById("form-repartidor");
  const repartidorId = document.getElementById("repartidor-id-input");
  const repartidorNombre = document.getElementById("repartidor-nombre-input");
  const repartidorApellido = document.getElementById("repartidor-apellido-input");
  const repartidorCorreo = document.getElementById("repartidor-correo-input");
  const repartidorTelefono = document.getElementById("repartidor-telefono-input");
  const repartidorPlaca = document.getElementById("repartidor-placa-input");
  const repartidorContrasenaInput = document.getElementById("repartidor-contrasena-input");
  const repartidorContrasenaGroup = document.getElementById("repartidor-contrasena-group");

  const modalContrasenaElement = document.getElementById("modal-contrasena");
  let modalContrasenaInstance = null; // Inicializar a null

  const contrasenaRepartidorId = document.getElementById("contrasena-repartidor-id");
  const nuevaContrasena = document.getElementById("nueva-contrasena-input");
  const confirmarContrasena = document.getElementById("confirmar-contrasena-input");

  let repartidores = [];

  // INICIALIZACIÓN DE INSTANCIAS DE MODAL DENTRO DEL DOMContentLoaded
  // Y COMPROBANDO QUE EL ELEMENTO EXISTA
  if (modalRepartidorElement) {
    modalRepartidorInstance = new bootstrap.Modal(modalRepartidorElement, {
      backdrop: 'static',
      keyboard: false
    });
    // Listeners para el botón 'X' y 'Cerrar' del modal de repartidor
    modalRepartidorElement.querySelector('.modal-header .btn-close')?.addEventListener('click', () => {
      console.log("Clic en botón 'X' del modal de repartidor.");
      modalRepartidorInstance.hide();
    });
    modalRepartidorElement.querySelector('.modal-footer .btn-secondary')?.addEventListener('click', () => {
      console.log("Clic en botón 'Cerrar' del modal de repartidor.");
      modalRepartidorInstance.hide();
    });
  }

  if (modalContrasenaElement) {
    modalContrasenaInstance = new bootstrap.Modal(modalContrasenaElement, {
      backdrop: 'static',
      keyboard: false
    });
    // Listeners para el botón 'X' y 'Cerrar' del modal de contraseña
    modalContrasenaElement.querySelector('.modal-header .btn-close')?.addEventListener('click', () => {
      console.log("Clic en botón 'X' del modal de contraseña.");
      modalContrasenaInstance.hide();
    });
    modalContrasenaElement.querySelector('.modal-footer .btn-secondary')?.addEventListener('click', () => {
      console.log("Clic en botón 'Cerrar' del modal de contraseña.");
      modalContrasenaInstance.hide();
    });
  }


  // Eventos para el modal de Repartidor
  // Estos listeners ya estaban bien, solo asegúrate de que modalRepartidorInstance
  // haya sido inicializado correctamente antes.
  if (modalRepartidorElement) {
    modalRepartidorElement.addEventListener('show.bs.modal', (event) => {
      const button = event.relatedTarget;
      const isEditButton = button && button.classList.contains('edit-repartidor-btn');

      formRepartidor.reset();
      repartidorId.value = "";

      if (isEditButton) {
        repartidorContrasenaGroup.style.display = "none";
        const id = Number.parseInt(button.dataset.id);
        const repartidor = repartidores.find((r) => r.idRepartidor === id);
        if (repartidor) {
          repartidorId.value = repartidor.idRepartidor;
          repartidorNombre.value = repartidor.nombre;
          repartidorApellido.value = repartidor.apellido;
          repartidorCorreo.value = repartidor.correoElectronico;
          repartidorTelefono.value = repartidor.telefono;
          repartidorPlaca.value = repartidor.placa || "";
        }
      } else {
        repartidorContrasenaGroup.style.display = "block";
        repartidorContrasenaInput.value = "";
      }
    });

    modalRepartidorElement.addEventListener('hidden.bs.modal', () => {
      formRepartidor.reset();
    });
  }

  // Eventos para el modal de Contraseña
  // Estos listeners ya estaban bien.
  if (modalContrasenaElement) {
    modalContrasenaElement.addEventListener('show.bs.modal', (event) => {
      const button = event.relatedTarget;
      if (button && button.classList.contains('change-password-btn')) {
        const id = Number.parseInt(button.dataset.id);
        contrasenaRepartidorId.value = id;
        nuevaContrasena.value = "";
        confirmarContrasena.value = "";
      }
    });
  }

  if (guardarRepartidorBtn) {
    guardarRepartidorBtn.addEventListener("click", () => saveRepartidor());
  }

  if (guardarContrasenaBtn) {
    guardarContrasenaBtn.addEventListener("click", () => saveContrasena());
  }

  if (btnAplicarFiltros) {
    btnAplicarFiltros.addEventListener("click", () => aplicarFiltros());
  }

  if (btnClearFiltros) {
    btnClearFiltros.addEventListener("click", () => clearFilters());
  }

  if (filtroNombre) {
    filtroNombre.addEventListener("input", () => aplicarFiltros());
  }

  async function saveRepartidor() {
    const repartidorIdValue = repartidorId.value;
    const isEditing = !!repartidorIdValue;

    const repartidorData = {
      nombre: repartidorNombre.value,
      apellido: repartidorApellido.value,
      correoElectronico: repartidorCorreo.value,
      telefono: repartidorTelefono.value,
      placa: repartidorPlaca.value,
    };

    if (!isEditing) {
      if (!repartidorContrasenaInput.value) {
        alert("La contraseña es obligatoria para nuevos repartidores.");
        return;
      }
      repartidorData.contrasena = repartidorContrasenaInput.value;
    }

    try {
      const url = isEditing ? `${API_BASE_URL}/repartidores/${repartidorIdValue}` : `${API_BASE_URL}/repartidores`;
      const method = isEditing ? "PUT" : "POST";

      const response = await fetch(url, {
        method: method,
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(repartidorData),
      });

      if (!response.ok) {
        const errorText = await response.text();
        throw new Error("Error al guardar repartidor: " + errorText);
      }

      // Cerrar el modal después de guardar exitosamente
      if (modalRepartidorInstance) { // Añadir esta comprobación de seguridad
        modalRepartidorInstance.hide();
      }

      await fetchRepartidores();
      aplicarFiltros();
      alert("Repartidor guardado exitosamente.");
    } catch (error) {
      console.error("Error al guardar repartidor:", error);
      alert("Error al guardar repartidor: " + error.message);
    }
  }

  async function saveContrasena() {
    const id = contrasenaRepartidorId.value;
    const nueva = nuevaContrasena.value;
    const confirmar = confirmarContrasena.value;

    if (!nueva || !confirmar) {
      alert("Todos los campos de contraseña son obligatorios.");
      return;
    }
    if (nueva !== confirmar) {
      alert("Las contraseñas no coinciden.");
      return;
    }
    if (nueva.length < 6) {
      alert("La contraseña debe tener al menos 6 caracteres.");
      return;
    }

    try {
      const response = await fetch(`${API_BASE_URL}/repartidores/${id}/password`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ contrasena: nueva }),
      });

      if (!response.ok) {
        const errorText = await response.text();
        throw new Error("Error al actualizar contraseña: " + errorText);
      }

      // Cerrar el modal después de guardar exitosamente
      if (modalContrasenaInstance) { // Añadir esta comprobación de seguridad
        modalContrasenaInstance.hide();
      }

      alert("Contraseña actualizada exitosamente.");
    } catch (error) {
      console.error("Error al guardar contraseña:", error);
      alert("Error al guardar contraseña: " + error.message);
    }
  }

  async function fetchRepartidores() {
    try {
      const response = await fetch(`${API_BASE_URL}/repartidores`);
      if (!response.ok) {
        throw new Error("Error al obtener repartidores");
      }
      repartidores = await response.json();
      aplicarFiltros();
    } catch (error) {
      console.error("Error fetching repartidores:", error);
      tablaRepartidoresBody.innerHTML =
        '<tr><td colspan="6" class="text-center py-4 text-danger">Error al cargar los repartidores.</td></tr>';
    }
  }

  function renderRepartidores(repartidoresToRender) {
    tablaRepartidoresBody.innerHTML = "";

    if (repartidoresToRender.length === 0) {
      tablaRepartidoresBody.innerHTML =
        '<tr><td colspan="6" class="text-center">No hay repartidores disponibles con los filtros aplicados.</td></tr>';
      return;
    }

    repartidoresToRender.forEach((repartidor) => {
      const row = document.createElement("tr");
      row.innerHTML = `
                <td>${repartidor.idRepartidor}</td>
                <td>${repartidor.nombre} ${repartidor.apellido}</td>
                <td>${repartidor.correoElectronico}</td>
                <td>${repartidor.telefono}</td>
                <td>${repartidor.placa || "N/A"}</td>
                <td>
                    <button class="btn btn-sm btn-info edit-repartidor-btn" data-id="${repartidor.idRepartidor}" data-bs-toggle="modal" data-bs-target="#modal-repartidor"><i class="fas fa-edit"></i> Editar</button>
                    <button class="btn btn-sm btn-secondary change-password-btn" data-id="${repartidor.idRepartidor}" data-bs-toggle="modal" data-bs-target="#modal-contrasena"><i class="fas fa-key"></i> Contraseña</button>
                    <button class="btn btn-sm btn-danger delete-repartidor-btn" data-id="${repartidor.idRepartidor}"><i class="fas fa-trash"></i> Eliminar</button>
                </td>
            `;
      tablaRepartidoresBody.appendChild(row);
    });

    tablaRepartidoresBody.querySelectorAll(".delete-repartidor-btn").forEach((button) => {
      button.addEventListener("click", (e) => deleteRepartidor(Number.parseInt(e.currentTarget.dataset.id)));
    });
  }

  function aplicarFiltros() {
    const nombreFiltro = filtroNombre ? filtroNombre.value.toLowerCase() : "";

    const repartidoresFiltrados = repartidores.filter((repartidor) => {
      return repartidor.nombre.toLowerCase().includes(nombreFiltro);
    });

    renderRepartidores(repartidoresFiltrados);
  }

  function clearFilters() {
    if (filtroNombre) filtroNombre.value = "";
    aplicarFiltros();
  }

  async function deleteRepartidor(id) {
    if (!confirm("¿Estás seguro de que quieres eliminar este repartidor?")) {
      return;
    }

    try {
      const response = await fetch(`${API_BASE_URL}/repartidores/${id}`, {
        method: "DELETE",
      });

      if (!response.ok) {
        throw new Error("Error al eliminar repartidor");
      }

      await fetchRepartidores();
      aplicarFiltros();
      alert("Repartidor eliminado exitosamente.");
    } catch (error) {
      console.error("Error deleting repartidor:", error);
      alert("Error al eliminar repartidor: " + error.message);
    }
  }

  fetchRepartidores();

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