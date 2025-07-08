let currentUser = null;
const bootstrap = window.bootstrap;

const API_BASE_URL = "http://localhost:8080";

document.addEventListener("DOMContentLoaded", () => {
    initializeLoginPage();
});

function initializeLoginPage() {
    setupEventListeners();
    // Esta función redirige si ya hay una sesión. Es importante que se ejecute antes de cualquier interacción de login.
    checkExistingSession(); 
    const contenedor = document.getElementById("contenedor");
    const registrarseBtn = document.getElementById("registrarse");
    const iniciarSesionBtn = document.getElementById("iniciarSesion");

    if (registrarseBtn && contenedor) {
        registrarseBtn.addEventListener("click", () => {
            contenedor.classList.add("activo");
            clearFormErrors(document.getElementById('formularioInicioSesion'));
            // Limpiar también el formulario de registro al cambiar a él si ya se había interactuado
            // clearFormErrors(document.getElementById('formularioRegistro')); 
        });
    }

    if (iniciarSesionBtn && contenedor) {
        iniciarSesionBtn.addEventListener("click", () => {
            contenedor.classList.remove("activo");
            clearFormErrors(document.getElementById('formularioRegistro'));
            // Limpiar también el formulario de inicio de sesión al cambiar a él si ya se había interactuado
            // clearFormErrors(document.getElementById('formularioInicioSesion'));
        });
    }
}

function setupEventListeners() {
    const loginForm = document.getElementById("formularioInicioSesion");
    if (loginForm) {
        loginForm.addEventListener("submit", handleUserLogin); 
    } else {
        console.error("No se encontró el formulario de inicio de sesión con ID 'formularioInicioSesion'.");
    }

    const registerForm = document.getElementById("formularioRegistro");
    if (registerForm) {
        registerForm.addEventListener("submit", handleUserRegistration); 
    } else {
        console.error("No se encontró el formulario de registro con ID 'formularioRegistro'.");
    }

    const forgotPasswordForm = document.getElementById("forgotPasswordForm");
    if (forgotPasswordForm) {
        forgotPasswordForm.addEventListener("submit", handleForgotPassword);
    }

    const adminForm = document.getElementById("adminForm");
    if (adminForm) {
        adminForm.addEventListener("submit", handleAdminLogin);
    } else {
        console.warn("No se encontró el formulario de administrador con ID 'adminForm'.");
    }

    const deliveryForm = document.getElementById("deliveryForm"); 
    if (deliveryForm) {
        deliveryForm.addEventListener("submit", handleDeliveryLogin);
    } else {
        console.warn("No se encontró el formulario de repartidor con ID 'deliveryForm'.");
    }

    setupRealTimeValidation();

    const registerPasswordInput = document.getElementById("contraseña");
    const confirmPasswordInput = document.getElementById("confirmarContraseña"); 

    if (confirmPasswordInput && registerPasswordInput) {
        confirmPasswordInput.addEventListener("input", checkPasswordMatch);
        registerPasswordInput.addEventListener("input", function() {
            checkPasswordStrength(this.value);
            checkPasswordMatch();
        });
    }

    setupPasswordToggle("togglePasswordInicio", "contraseñaInicio");
    setupPasswordToggle("togglePasswordRegistro", "contraseña");
    setupPasswordToggle("toggleConfirmPasswordRegistro", "confirmarContraseña");
    setupPasswordToggle("toggleAdminPassword", "adminPassword");
    setupPasswordToggle("toggleDeliveryPassword", "deliveryPassword");
}

function setupPasswordToggle(buttonId, inputId) {
    const button = document.getElementById(buttonId);
    const input = document.getElementById(inputId);

    if (button && input) {
        button.addEventListener("click", function() {
            const type = input.getAttribute("type") === "password" ? "text" : "password";
            input.setAttribute("type", type);

            const icon = this.querySelector("i");
            if (icon) {
                icon.classList.toggle("fa-eye");
                icon.classList.toggle("fa-eye-slash");
            }
        });
    } else {
        console.warn(`Toggle button (${buttonId}) or input (${inputId}) not found for password toggle.`);
    }
}

function clearFormErrors(form) {
    form.querySelectorAll('.is-invalid').forEach(el => {
        el.classList.remove('is-invalid');
        const formControl = el.closest(".form-control");
        const small = formControl ? formControl.querySelector("small") : null;
        if (formControl) formControl.classList.remove("error");
        if (small) small.innerText = "";
    });
    form.querySelectorAll(".alert").forEach(alert => alert.remove());
}


async function handleAdminLogin(e) {
    e.preventDefault();
    const adminEmailInput = document.getElementById("adminEmail");
    const adminPasswordInput = document.getElementById("adminPassword");
    const adminModalElement = document.getElementById("adminModal");
    const adminModal = bootstrap.Modal.getInstance(adminModalElement) || new bootstrap.Modal(adminModalElement);
    const adminModalBody = adminModalElement.querySelector(".modal-body");

    clearFormErrors(adminForm);

    const usuario = adminEmailInput.value.trim();
    const contrasena = adminPasswordInput.value.trim();

    let isValid = true;
    if (!validateField(adminEmailInput, usuario !== "", "El usuario es obligatorio.")) {
        isValid = false;
    }
    if (!validateField(adminPasswordInput, contrasena !== "", "La contraseña es obligatoria.")) {
        isValid = false;
    }

    if (!isValid) {
        return;
    }

    const submitBtn = e.target.querySelector('button[type="submit"]');
    setLoadingState(submitBtn, true);
    showAlert("info", "Iniciando sesión de administrador...", adminModalBody);

    try {
        const response = await fetch(`${API_BASE_URL}/api/administradores/login`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ usuario, contrasena })
        });

        if (response.ok) {
            const adminData = await response.json();
            localStorage.setItem('userRole', 'administrador');
            localStorage.setItem('userData', JSON.stringify(adminData));
            console.log("Admin Data:", adminData); 
            if (adminData.idAdministrador) { 
                localStorage.setItem("farmalineAdminId", adminData.idAdministrador);
            }
            showAlert("success", `¡Bienvenido, Administrador ${adminData.usuario || adminData.nombre || ''}! Acceso concedido.`, adminModalBody);
            setTimeout(() => {
                adminModal.hide();
                window.location.href = "admin-inicio.html";
            }, 1500);
        } else if (response.status === 401) {
            showAlert("danger", "Credenciales incorrectas para Administrador.", adminModalBody);
            validateField(adminEmailInput, false, "Usuario o contraseña incorrectos.");
            validateField(adminPasswordInput, false, "Usuario o contraseña incorrectos.");
        } else {
            const errorData = await response.text();
            showAlert("danger", `Error al iniciar sesión de administrador: ${errorData || response.statusText}`, adminModalBody);
            console.error("Admin login error (server response):", response.status, errorData);
        }
    } catch (error) {
        showAlert("danger", "Error de conexión. No se pudo comunicar con el servidor.", adminModalBody);
        console.error("Admin login fetch error:", error);
    } finally {
        setLoadingState(submitBtn, false);
    }
}

async function handleDeliveryLogin(e) {
    e.preventDefault();
    const deliveryEmailInput = document.getElementById("deliveryEmail");
    const deliveryPasswordInput = document.getElementById("deliveryPassword");
    const deliveryModalElement = document.getElementById("deliveryModal");
    const deliveryModal = bootstrap.Modal.getInstance(deliveryModalElement) || new bootstrap.Modal(deliveryModalElement);
    const deliveryModalBody = deliveryModalElement.querySelector(".modal-body");

    clearFormErrors(deliveryForm);

    const correoElectronico = deliveryEmailInput.value.trim();
    const contrasena = deliveryPasswordInput.value.trim();

    let isValid = true;
    if (!validateField(deliveryEmailInput, isEmail(correoElectronico), "Ingrese un correo válido.")) {
        isValid = false;
    }
    if (!validateField(deliveryPasswordInput, contrasena !== "", "La contraseña es obligatoria.")) {
        isValid = false;
    }

    if (!isValid) {
        return;
    }

    const submitBtn = e.target.querySelector('button[type="submit"]');
    setLoadingState(submitBtn, true);
    showAlert("info", "Iniciando sesión de repartidor...", deliveryModalBody);

    try {
        const response = await fetch(`${API_BASE_URL}/api/repartidores/login`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ correoElectronico, contrasena })
        });

        if (response.ok) {
            const deliveryData = await response.json();
            // *** ESTAS LÍNEAS SON LAS QUE GUARDAN EN LOCALSTORAGE ***
            localStorage.setItem('userRole', 'repartidor');
            localStorage.setItem('userData', JSON.stringify(deliveryData));
            console.log("Delivery Data:", deliveryData); 
            if (deliveryData.idRepartidor) { 
                localStorage.setItem("farmalineRepartidorId", deliveryData.idRepartidor);
            }
            showAlert("success", `¡Bienvenido, Repartidor ${deliveryData.nombre || deliveryData.correoElectronico || ''}! Acceso concedido.`, deliveryModalBody);
            setTimeout(() => {
                deliveryModal.hide();
                window.location.href = "repartidor-inicio.html";
            }, 1500);
        } else if (response.status === 401) {
            showAlert("danger", "Credenciales incorrectas para Repartidor.", deliveryModalBody);
            validateField(deliveryEmailInput, false, "Correo o contraseña incorrectos.");
            validateField(deliveryPasswordInput, false, "Correo o contraseña incorrectos.");
        } else {
            const errorData = await response.text();
            showAlert("danger", `Error al iniciar sesión de repartidor: ${errorData || response.statusText}`, deliveryModalBody);
            console.error("Delivery login error (server response):", response.status, errorData);
        }
    } catch (error) {
        showAlert("danger", "Error de conexión. No se pudo comunicar con el servidor.", deliveryModalBody);
        console.error("Delivery login fetch error:", error);
    } finally {
        setLoadingState(submitBtn, false);
    }
}

async function handleUserLogin(e) { 
    e.preventDefault();

    const form = e.target;
    const emailInput = document.getElementById("correoInicio");
    const passwordInput = document.getElementById("contraseñaInicio");

    clearFormErrors(form);

    let isFormValid = true;
    if (!validateField(emailInput, isEmail(emailInput.value.trim()), "Correo no válido.")) {
        isFormValid = false;
    }
    if (!validateField(passwordInput, passwordInput.value.trim().length >= 8, "La contraseña debe tener al menos 8 caracteres.")) {
        isFormValid = false;
    }

    if (!isFormValid) {
        return;
    }

    const correoElectronico = emailInput.value.trim();
    const contrasena = passwordInput.value.trim();

    const submitBtn = form.querySelector('button[type="submit"]');
    setLoadingState(submitBtn, true);
    showAlert("info", "Iniciando sesión...");

    try {
        const response = await fetch(`${API_BASE_URL}/api/usuarios/login`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ correoElectronico, contrasena }),
        });

        if (response.ok) {
            const userData = await response.json();
            // *** ESTAS LÍNEAS SON LAS QUE GUARDAN EN LOCALSTORAGE ***
            localStorage.setItem('userRole', 'usuario');
            localStorage.setItem("userData", JSON.stringify(userData));
            console.log("User Data:", userData); 
            if (userData.idUsuario) { 
                localStorage.setItem("farmalineUserId", userData.idUsuario);
            }
            currentUser = userData;

            showAlert("success", `¡Bienvenido, ${userData.nombre || userData.correoElectronico}! Inicio de sesión exitoso.`);

            setTimeout(() => {
                window.location.href = "Index.html";
            }, 1500);

        } else if (response.status === 401) {
            showAlert("danger", "Credenciales incorrectas. Por favor verifica tu email y contraseña.");
            validateField(emailInput, false, "Correo o contraseña incorrectos.");
            validateField(passwordInput, false, "Correo o contraseña incorrectos.");
        } else {
            const errorData = await response.text();
            showAlert("danger", `Error al iniciar sesión: ${errorData || response.statusText}.`);
            console.error("Login error (server response):", response.status, errorData);
        }
    } catch (error) {
        showAlert("danger", "Error de conexión. No se pudo comunicar con el servidor. Intenta de nuevo más tarde.");
        console.error("Login fetch error:", error);
    } finally {
        setLoadingState(submitBtn, false);
    }
}

async function handleUserRegistration(e) { 
    e.preventDefault();

    const form = e.target;
    const nombreInput = document.getElementById("nombre");
    const apellidoInput = document.getElementById("apellido"); 
    const correoInput = document.getElementById("correo");
    const domicilioInput = document.getElementById("direccion"); 
    const contrasenaInput = document.getElementById("contraseña");
    const confirmarContrasenaInput = document.getElementById("confirmarContraseña"); 
    const telefonoInput = document.getElementById("telefono"); 
    const terminosInput = document.getElementById("terminos");

    clearFormErrors(form);

    let isFormValid = true;
    if (!validateField(nombreInput, nombreInput.value.trim() !== "", "El nombre no puede estar vacío.")) { isFormValid = false; }
    if (!validateField(apellidoInput, apellidoInput.value.trim() !== "", "El apellido no puede estar vacío.")) { isFormValid = false; }
    if (!validateField(correoInput, isEmail(correoInput.value.trim()), "Correo no válido.")) { isFormValid = false; }
    if (!validateField(domicilioInput, domicilioInput.value.trim() !== "", "El domicilio no puede estar vacío.")) { isFormValid = false; }
    if (!validateField(contrasenaInput, contrasenaInput.value.trim().length >= 8, "La contraseña debe tener al menos 8 caracteres.")) { isFormValid = false; }
    if (!validateField(confirmarContrasenaInput, contrasenaInput.value.trim() === confirmarContrasenaInput.value.trim(), "Las contraseñas no coinciden.")) { isFormValid = false; }
    if (!validateField(telefonoInput, telefonoInput.value.trim() !== "", "El teléfono no puede estar vacío.")) { isFormValid = false; }
    if (!terminosInput.checked) {
        showAlert("danger", "Debe aceptar los términos y condiciones.", form);
        isFormValid = false;
    }

    if (!isFormValid) {
        return;
    }

    const formData = {
        nombre: nombreInput.value.trim(),
        apellido: apellidoInput.value.trim(),
        correoElectronico: correoInput.value.trim(),
        domicilio: domicilioInput.value.trim(),
        contrasena: contrasenaInput.value.trim(),
        telefono: telefonoInput.value.trim()
    };

    const submitBtn = form.querySelector('button[type="submit"]');
    setLoadingState(submitBtn, true);
    showAlert("info", "Registrando usuario...");

    try {
        const response = await fetch(`${API_BASE_URL}/api/usuarios`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(formData),
        });

        if (response.ok && response.status === 201) {
            const newUserData = await response.json();
            showAlert("success", "¡Registro exitoso! Ya puedes iniciar sesión con tu cuenta.");

            form.reset();
            form.classList.remove("was-validated");

            setTimeout(() => {
                const contenedor = document.getElementById("contenedor");
                if (contenedor) {
                    contenedor.classList.remove("activo");
                }
                const correoInicioInput = document.getElementById("correoInicio");
                if (correoInicioInput) {
                    correoInicioInput.value = newUserData.correoElectronico;
                }
            }, 1500);

        } else if (response.status === 400) {
            const errorData = await response.text();
            showAlert("danger", `Error de registro: ${errorData || "Datos inválidos o correo ya registrado."}`);
        } else {
            showAlert("danger", `Error al crear la cuenta. Código: ${response.status}. Por favor intenta nuevamente.`);
            console.error("Registration error (server response):", response.status, await response.text());
        }
    } catch (error) {
        showAlert("danger", "Error de conexión. No se pudo comunicar con el servidor para registrarte.");
        console.error("Registration fetch error:", error);
    } finally {
        setLoadingState(submitBtn, false);
    }
}

async function handleForgotPassword(e) {
    e.preventDefault();

    const emailInput = document.getElementById("forgotEmail");
    const email = emailInput.value.trim();
    const submitBtn = e.target.querySelector('button[type="submit"]');

    clearFormErrors(e.target);

    if (!validateField(emailInput, isEmail(email), "Por favor, ingresa un correo electrónico válido.")) {
        return;
    }

    setLoadingState(submitBtn, true);
    showAlert("info", "Enviando enlace de recuperación...");

    try {
        await simulateAPICall(); // Esto es solo una simulación, no un endpoint real

        showAlert("success", "Se ha enviado un enlace de recuperación a tu correo electrónico.");

        const modalElement = document.getElementById("forgotPasswordModal");
        const modal = bootstrap.Modal.getInstance(modalElement) || new bootstrap.Modal(modalElement);
        modal.hide();

        document.getElementById("forgotPasswordForm").reset();
    } catch (error) {
        showAlert("danger", "Error al enviar el enlace. Por favor intenta nuevamente.");
    } finally {
        setLoadingState(submitBtn, false);
    }
}

function setupRealTimeValidation() {
    const loginInputs = document.querySelectorAll("#formularioInicioSesion input[required]");
    const registerInputs = document.querySelectorAll("#formularioRegistro input[required]");
    const adminInputs = document.querySelectorAll("#adminForm input[required]");
    const deliveryInputs = document.querySelectorAll("#deliveryForm input[required]");
    const forgotInputs = document.querySelectorAll("#forgotPasswordForm input[required]");

    [...loginInputs, ...registerInputs, ...adminInputs, ...deliveryInputs, ...forgotInputs].forEach((input) => {
        input.addEventListener("blur", function() {
            let isValid = true;
            let errorMessage = "Este campo no puede estar vacío";

            if (this.type === "email") {
                isValid = isEmail(this.value.trim());
                errorMessage = "Correo no válido";
            } else if (this.type === "password") {
                isValid = this.value.trim().length >= 8;
                errorMessage = "La contraseña debe tener al menos 8 caracteres";
            } else if (this.id === "nombre" || this.id === "apellido" || this.id === "direccion" || this.id === "telefono" || this.id === "adminEmail" || this.id === "deliveryEmail") {
                isValid = this.value.trim() !== "";
            } else if (this.id === "confirmarContraseña") {
                isValid = this.value.trim() === document.getElementById("contraseña").value.trim();
                errorMessage = "Las contraseñas no coinciden.";
                if (this.value.trim() === "" && document.getElementById("contraseña").value.trim() !== "") {
                    errorMessage = "Confirma tu contraseña.";
                    isValid = false;
                }
            }

            validateField(this, isValid, errorMessage);
        });

        input.addEventListener("input", function() {
            if (this.classList.contains("is-invalid")) {
                 let isValid = true;
                 let errorMessage = "Este campo no puede estar vacío";

                 if (this.type === "email") {
                     isValid = isEmail(this.value.trim());
                     errorMessage = "Correo no válido";
                 } else if (this.type === "password") {
                     isValid = this.value.trim().length >= 8;
                     errorMessage = "La contraseña debe tener al menos 8 caracteres";
                 } else if (this.id === "nombre" || this.id === "apellido" || this.id === "direccion" || this.id === "telefono" || this.id === "adminEmail" || this.id === "deliveryEmail") {
                     isValid = this.value.trim() !== "";
                 } else if (this.id === "confirmarContraseña") {
                     isValid = this.value.trim() === document.getElementById("contraseña").value.trim();
                     errorMessage = "Las contraseñas no coinciden.";
                     if (this.value.trim() === "" && document.getElementById("contraseña").value.trim() !== "") {
                         errorMessage = "Confirma tu contraseña.";
                         isValid = false;
                     }
                 }
                validateField(this, isValid, errorMessage);
            }
        });
    });
}


function validateField(field, condition, errorMessage) {
    const formControl = field.closest(".form-control");
    const small = formControl ? formControl.querySelector("small") : null;

    if (condition) {
        field.classList.remove("is-invalid");
        field.classList.add("is-valid");
        if (formControl) formControl.classList.remove("error");
        if (small) small.innerText = "";
        return true;
    } else {
        field.classList.remove("is-valid");
        field.classList.add("is-invalid");
        if (formControl) formControl.classList.add("error");
        if (small) small.innerText = errorMessage;
        return false;
    }
}

function checkPasswordStrength(password) {
    const passwordInput = document.getElementById("contraseña");
    if (!passwordInput) return;

    let strengthIndicator = passwordInput.closest(".form-control")?.querySelector(".password-strength");

    if (!strengthIndicator) {
        strengthIndicator = document.createElement("div");
        strengthIndicator.className = "password-strength";
        passwordInput.parentNode.appendChild(strengthIndicator);
    }

    let strength = 0;
    if (password.length >= 8) strength++;
    if (password.match(/[a-z]/) && password.match(/[A-Z]/)) strength++;
    if (password.match(/[0-9]/)) strength++;
    if (password.match(/[^a-zA-Z0-9]/)) strength++;

    if (strengthIndicator) {
        strengthIndicator.className = "password-strength";
        if (password.length === 0) {
            strengthIndicator.innerText = "";
        } else if (strength <= 1) {
            strengthIndicator.classList.add("weak");
            strengthIndicator.innerText = "Débil";
        } else if (strength <= 3) {
            strengthIndicator.classList.add("medium");
            strengthIndicator.innerText = "Media";
        } else {
            strengthIndicator.classList.add("strong");
            strengthIndicator.innerText = "Fuerte";
        }
    }
}

function checkPasswordMatch() {
    const passwordInput = document.getElementById("contraseña");
    const confirmPasswordInput = document.getElementById("confirmarContraseña");

    if (!passwordInput || !confirmPasswordInput) return;

    const password = passwordInput.value.trim();
    const confirmPassword = confirmPasswordInput.value.trim();

    const match = password === confirmPassword;

    if (confirmPassword.length > 0) {
        validateField(confirmPasswordInput, match, "Las contraseñas no coinciden.");
    } else if (password.length > 0 && confirmPassword.length === 0) {
        validateField(confirmPasswordInput, false, "Confirma tu contraseña.");
    } else {
        validateField(confirmPasswordInput, true, "");
    }

    return match;
}

function checkExistingSession() {
    const userRole = localStorage.getItem("userRole");
    const userData = localStorage.getItem("userData");

    if (userRole && userData) {
        try {
            currentUser = JSON.parse(userData);
            // Redirige solo si no estamos ya en la página de destino para evitar bucles.
            // Considera si la página actual ya es la de destino antes de redirigir.
            const currentPath = window.location.pathname.split('/').pop();

            if (userRole === 'administrador') {
                if (currentPath !== "admin-inicio.html") {
                    window.location.href = "admin-inicio.html";
                }
            } else if (userRole === 'repartidor') {
                if (currentPath !== "repartidor-inicio.html") {
                    window.location.href = "repartidor-inicio.html"; 
                }
            } else if (userRole === 'usuario') {
                if (currentPath !== "Index.html") {
                    window.location.href = "Index.html"; 
                }
            }
        } catch (error) {
            console.error("Error al parsear datos de sesión desde localStorage:", error);
            clearSession(); // Limpia la sesión si los datos están corruptos
        }
    }
}

// Función auxiliar para limpiar la sesión (útil si userData está corrupto)
function clearSession() {
    localStorage.removeItem("userRole");
    localStorage.removeItem("userData");
    localStorage.removeItem("farmalineUserId");
    localStorage.removeItem("farmalineAdminId");
    localStorage.removeItem("farmalineRepartidorId");
    currentUser = null;
}

async function simulateAPICall() {
    return new Promise((resolve) => {
        setTimeout(resolve, 500 + Math.random() * 500);
    });
}

function setLoadingState(button, loading) {
    if (button) {
        if (loading) {
            button.classList.add("loading");
            button.disabled = true;
            button.innerHTML = '<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span> Cargando...';
        } else {
            button.classList.remove("loading");
            button.disabled = false;
            // Restaurar el texto original del botón según su formulario
            if (button.closest('#adminForm')) {
                button.innerHTML = '<i class="fas fa-sign-in-alt me-2"></i> Ingresar como Administrador';
            } else if (button.closest('#deliveryForm')) {
                button.innerHTML = '<i class="fas fa-sign-in-alt me-2"></i> Ingresar como Repartidor';
            } else if (button.closest('#formularioInicioSesion')) {
                button.innerHTML = 'Ingresar';
            } else if (button.closest('#formularioRegistro')) {
                button.innerHTML = 'Registrarse';
            } else if (button.closest('#forgotPasswordForm')) {
                button.innerHTML = 'Enviar Enlace';
            }
        }
    }
}

function showAlert(type, message, container = null) {
    const targetContainer = container || document.querySelector(".login-section .contenedor");
    if (!targetContainer) {
        console.error("No se encontró el contenedor para las alertas.");
        return;
    }
    // Eliminar alertas existentes del mismo tipo o todas antes de añadir una nueva
    targetContainer.querySelectorAll(".alert").forEach((alert) => alert.remove());

    const alert = document.createElement("div");
    alert.className = `alert alert-${type} alert-dismissible fade show mt-3`;
    alert.innerHTML = `
        ${message}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    `;

    targetContainer.insertBefore(alert, targetContainer.firstChild);

    // Auto-eliminar la alerta después de un tiempo
    setTimeout(() => {
        if (alert.parentNode) {
            alert.remove();
        }
    }, 5000);
}

function logout() {
    localStorage.removeItem("userRole");
    localStorage.removeItem("userData");
    localStorage.removeItem("farmalineUserId");
    localStorage.removeItem("farmalineAdminId");
    localStorage.removeItem("farmalineRepartidorId");
    currentUser = null;
    window.location.href = "Login.html";
}

window.farmalineAuth = {
    logout,
    getCurrentUser: () => {
        const userDataString = localStorage.getItem("userData");
        if (userDataString) {
            return JSON.parse(userDataString);
        }
        return null;
    },
    isLoggedIn: () => {
        return localStorage.getItem("userRole") !== null && localStorage.getItem("userData") !== null;
    },
    getUserRole: () => localStorage.getItem("userRole")
};

function isEmail(email) {
    return /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,6}$/.test(email);
}