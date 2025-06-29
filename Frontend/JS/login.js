let currentUser = null;
const bootstrap = window.bootstrap;

const API_BASE_URL = "http://localhost:8080";
const LOGIN_ENDPOINT = `${API_BASE_URL}/api/usuarios/login`;
const REGISTER_ENDPOINT = `${API_BASE_URL}/api/usuarios/registrar`;

document.addEventListener("DOMContentLoaded", () => {
    initializeLoginPage();
});

function initializeLoginPage() {
    setupEventListeners();
    checkExistingSession();
    const contenedor = document.getElementById("contenedor");
    const registrarseBtn = document.getElementById("registrarse");
    const iniciarSesionBtn = document.getElementById("iniciarSesion");

    if (registrarseBtn && contenedor) {
        registrarseBtn.addEventListener("click", () => {
            contenedor.classList.add("activo");
        });
    }

    if (iniciarSesionBtn && contenedor) {
        iniciarSesionBtn.addEventListener("click", () => {
            contenedor.classList.remove("activo");
        });
    }
}

function setupEventListeners() {
    const loginForm = document.getElementById("formularioInicioSesion");
    if (loginForm) {
        loginForm.addEventListener("submit", handleLogin);
    } else {
        console.error("No se encontró el formulario de inicio de sesión con ID 'formularioInicioSesion'.");
    }

    const registerForm = document.getElementById("formularioRegistro");
    if (registerForm) {
        registerForm.addEventListener("submit", handleRegister);
    } else {
        console.error("No se encontró el formulario de registro con ID 'formularioRegistro'.");
    }

    const forgotPasswordForm = document.getElementById("forgotPasswordForm");
    if (forgotPasswordForm) {
        forgotPasswordForm.addEventListener("submit", handleForgotPassword);
    }

    // --- NUEVO: Manejo del formulario de Administrador ---
    const adminForm = document.getElementById("adminForm");
    if (adminForm) {
        adminForm.addEventListener("submit", handleAdminLogin);
    } else {
        console.warn("No se encontró el formulario de administrador con ID 'adminForm'.");
    }
    // --- FIN NUEVO ---

    setupRealTimeValidation();

    const confirmPasswordInput = document.getElementById("contraseña");
    const registerPasswordInput = document.getElementById("contraseña");
    if (confirmPasswordInput && registerPasswordInput) {
        confirmPasswordInput.addEventListener("input", checkPasswordMatch);
        registerPasswordInput.addEventListener("input", function() {
            checkPasswordStrength(this.value);
            checkPasswordMatch();
        });
    }
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

// --- NUEVO: Función para manejar el login del Administrador ---
async function handleAdminLogin(e) {
    e.preventDefault();

    const adminEmailInput = document.getElementById("adminEmail"); // Este será el "nombre" del administrador
    const adminPasswordInput = document.getElementById("adminPassword");
    const adminModalElement = document.getElementById("adminModal");
    const adminModal = bootstrap.Modal.getInstance(adminModalElement) || new bootstrap.Modal(adminModalElement);

    const adminName = adminEmailInput.value.trim();
    const adminPassword = adminPasswordInput.value.trim();

    // Validación básica: asegura que ambos campos no estén vacíos
    let isValid = true;
    if (adminName === "") {
        showAlert("danger", "El nombre de administrador no puede estar vacío.", adminModalElement.querySelector(".modal-body"));
        isValid = false;
    }
    if (adminPassword === "") {
        showAlert("danger", "La contraseña de administrador no puede estar vacía.", adminModalElement.querySelector(".modal-body"));
        isValid = false;
    }

    if (!isValid) {
        return;
    }

    const submitBtn = e.target.querySelector('button[type="submit"]');
    setLoadingState(submitBtn, true);
    showAlert("info", "Iniciando sesión de administrador...", adminModalElement.querySelector(".modal-body"));

    try {
        // --- AQUÍ DEBERÍAS REEMPLAZAR CON TU LÓGICA DE AUTENTICACIÓN REAL DEL ADMINISTRADOR ---
        // Ejemplo de simulación:
        await simulateAPICall(); // Simula una llamada a la API
        
        // Simulación de credenciales: Reemplaza esto con una llamada a tu backend real
        const MOCK_ADMIN_USER = "admin";
        const MOCK_ADMIN_PASS = "admin123"; // Cambia esto por una contraseña segura o un hash

        if (adminName === MOCK_ADMIN_USER && adminPassword === MOCK_ADMIN_PASS) {
            localStorage.setItem("farmalineAdminId", adminName); // Guarda el nombre como ID en localStorage
            adminModal.hide(); // Oculta el modal
            showAlert("success", `¡Bienvenido, ${adminName}! Acceso de administrador concedido.`);
            setTimeout(() => {
                window.location.href = "Admin.html"; // Redirige al panel de administrador
            }, 1500);
        } else {
            showAlert("danger", "Credenciales de administrador incorrectas.", adminModalElement.querySelector(".modal-body"));
        }

    } catch (error) {
        showAlert("danger", "Error de conexión. No se pudo comunicar con el servidor de administrador.", adminModalElement.querySelector(".modal-body"));
        console.error("Admin login fetch error:", error);
    } finally {
        setLoadingState(submitBtn, false);
    }
}
// --- FIN NUEVO ---

async function handleLogin(e) {
    e.preventDefault();

    const form = e.target;
    const emailInput = document.getElementById("correoInicio");
    const passwordInput = document.getElementById("contraseñaInicio");

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

    const email = emailInput.value.trim();
    const password = passwordInput.value.trim();

    const submitBtn = form.querySelector('button[type="submit"]');
    setLoadingState(submitBtn, true);
    showAlert("info", "Iniciando sesión...");

    try {
        const response = await fetch(LOGIN_ENDPOINT, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ correoElectronico: email, contrasena: password }),
        });

        if (response.ok) {
            const userData = await response.json();
            sessionStorage.setItem("farmalineUser", JSON.stringify(userData));
            if (userData.idUsuario) {
                localStorage.setItem("farmalineUserId", userData.idUsuario);
            }
            currentUser = userData;

            showAlert("success", `¡Bienvenido, ${userData.nombre || userData.email || userData.correoElectronico}! Inicio de sesión exitoso.`);

            setTimeout(() => {
                window.location.href = "Index.html";
            }, 1500);

        } else if (response.status === 401) {
            showAlert("danger", "Credenciales incorrectas. Por favor verifica tu email y contraseña.");
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

async function handleRegister(e) {
    e.preventDefault();

    const form = e.target;
    const nombreInput = document.getElementById("nombre");
    const correoInput = document.getElementById("correo");
    const passwordInput = document.getElementById("contraseña");

    let isFormValid = true;
    if (!validateField(nombreInput, nombreInput.value.trim() !== "", "El nombre no puede estar vacío.")) {
        isFormValid = false;
    }
    if (!validateField(correoInput, isEmail(correoInput.value.trim()), "Correo no válido.")) {
        isFormValid = false;
    }
    if (!validateField(passwordInput, passwordInput.value.trim().length >= 8, "La contraseña debe tener al menos 8 caracteres.")) {
        isFormValid = false;
    }

    if (!isFormValid) {
        return;
    }

    const formData = {
        nombre: nombreInput.value.trim(),
        correoElectronico: correoInput.value.trim(),
        password: passwordInput.value.trim(),
    };

    const submitBtn = form.querySelector('button[type="submit"]'); // Cambiado a type="submit" si es lo correcto en tu HTML
    setLoadingState(submitBtn, true);
    showAlert("info", "Registrando usuario...");

    try {
        const response = await fetch(REGISTER_ENDPOINT, {
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

    const email = document.getElementById("forgotEmail").value;
    const submitBtn = e.target.querySelector('button[type="submit"]');

    setLoadingState(submitBtn, true);
    showAlert("info", "Enviando enlace de recuperación...");

    try {
        await simulateAPICall();

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
    const loginInputs = document.querySelectorAll(".inicio-sesion input[required]");
    const registerInputs = document.querySelectorAll(".registro input[required]");
    const adminInputs = document.querySelectorAll("#adminForm input[required]"); // Seleccionar inputs del formulario de administrador
    const deliveryInputs = document.querySelectorAll("#deliveryForm input[required]"); // Seleccionar inputs del formulario de repartidor


    loginInputs.forEach((input) => {
        input.addEventListener("blur", function() {
            if (this.id === "correoInicio") {
                validateField(this, isEmail(this.value.trim()), "Correo no válido");
            } else if (this.id === "contraseñaInicio") {
                validateField(this, this.value.trim().length >= 8, "La contraseña debe tener al menos 8 caracteres");
            } else {
                validateField(this, this.value.trim() !== "", "Este campo no puede estar vacío");
            }
        });
        input.addEventListener("input", function() {
            if (this.classList.contains("is-invalid")) {
                if (this.id === "correoInicio") {
                    validateField(this, isEmail(this.value.trim()), "Correo no válido");
                } else if (this.id === "contraseñaInicio") {
                    validateField(this, this.value.trim().length >= 8, "La contraseña debe tener al menos 8 caracteres");
                } else {
                    validateField(this, this.value.trim() !== "", "Este campo no puede estar vacío");
                }
            }
        });
    });

    registerInputs.forEach((input) => {
        input.addEventListener("blur", function() {
            if (this.id === "nombre") {
                validateField(this, this.value.trim() !== "", "El nombre no puede estar vacío");
            } else if (this.id === "correo") {
                validateField(this, isEmail(this.value.trim()), "Correo no válido");
            } else if (this.id === "contraseña") {
                validateField(this, this.value.trim().length >= 8, "La contraseña debe tener al menos 8 caracteres");
            } else {
                validateField(this, this.value.trim() !== "", "Este campo no puede estar vacío");
            }
        });
        input.addEventListener("input", function() {
            if (this.classList.contains("is-invalid")) {
                if (this.id === "nombre") {
                    validateField(this, this.value.trim() !== "", "El nombre no puede estar vacío");
                } else if (this.id === "correo") {
                    validateField(this, isEmail(this.value.trim()), "Correo no válido");
                } else if (this.id === "contraseña") {
                    validateField(this, this.value.trim().length >= 8, "La contraseña debe tener al menos 8 caracteres");
                } else {
                    validateField(this, this.value.trim() !== "", "Este campo no puede estar vacío");
                }
            }
        });
    });

    // Validación en tiempo real para el formulario de administrador
    adminInputs.forEach((input) => {
        input.addEventListener("blur", function() {
            validateField(this, this.value.trim() !== "", "Este campo no puede estar vacío");
        });
        input.addEventListener("input", function() {
            if (this.classList.contains("is-invalid")) {
                validateField(this, this.value.trim() !== "", "Este campo no puede estar vacío");
            }
        });
    });

    // Validación en tiempo real para el formulario de repartidor (si lo deseas implementar)
    deliveryInputs.forEach((input) => {
        input.addEventListener("blur", function() {
            validateField(this, this.value.trim() !== "", "Este campo no puede estar vacío");
        });
        input.addEventListener("input", function() {
            if (this.classList.contains("is-invalid")) {
                validateField(this, this.value.trim() !== "", "Este campo no puede estar vacío");
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
    if (!passwordInput) return; // Salir si el input de contraseña no existe

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
        strengthIndicator.className = "password-strength"; // Reset classes
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

    // Aplica validación también al campo de confirmación de contraseña
    if (confirmPassword.length > 0) { // Solo si el usuario ha empezado a escribir en este campo
        validateField(confirmPasswordInput, match, "Las contraseñas no coinciden.");
    } else if (password.length > 0 && confirmPassword.length === 0) {
         validateField(confirmPasswordInput, false, "Confirma tu contraseña.");
    } else {
         validateField(confirmPasswordInput, true, ""); // Resetea el estado si ambos están vacíos
    }

    return match;
}

function checkExistingSession() {
    const userData = localStorage.getItem("farmalineUser") || sessionStorage.getItem("farmalineUser");
    const userId = localStorage.getItem("farmalineUserId");
    const adminId = localStorage.getItem("farmalineAdminId"); // Verificar si hay una sesión de administrador

    if (userData) {
        try {
            currentUser = JSON.parse(userData);
            if (userId && !currentUser.idUsuario) {
                currentUser.idUsuario = parseInt(userId);
            }
            window.location.href = "Index.html";
        } catch (error) {
            console.error("Error al parsear datos de sesión:", error);
            localStorage.removeItem("farmalineUser");
            sessionStorage.removeItem("farmalineUser");
            localStorage.removeItem("farmalineUserId");
        }
    } else if (adminId) { // Si hay una sesión de administrador, redirigir
        console.log(`Sesión de administrador activa para: ${adminId}`);
        // No hay un 'currentUser' para admin en esta estructura, pero se podría crear si es necesario
        window.location.href = "admin-panel.html"; // Redirige directamente al panel de admin
    }
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
            // Asegúrate de restaurar el texto original del botón según el formulario
            if (button.closest('#adminForm')) {
                button.innerHTML = '<i class="fas fa-sign-in-alt me-2"></i> Ingresar como Administrador';
            }
            else if (button.closest('#deliveryForm')) {
                button.innerHTML = '<i class="fas fa-sign-in-alt me-2"></i> Ingresar como Repartidor';
            }
            else if (button.closest('#formularioInicioSesion')) {
                button.innerHTML = 'Ingresar';
            } else if (button.closest('#formularioRegistro')) {
                button.innerHTML = 'Registrarse';
            } else if (button.closest('#forgotPasswordForm')) {
                button.innerHTML = 'Enviar Enlace';
            }
        }
    }
}

// Modificada para permitir un contenedor de alerta específico (útil para modales)
function showAlert(type, message, container = null) {
    // Eliminar alertas existentes del mismo contenedor
    const targetContainer = container || document.querySelector(".login-section .contenedor");
    if (!targetContainer) {
        console.error("No se encontró el contenedor para las alertas.");
        return;
    }
    const existingAlerts = targetContainer.querySelectorAll(".alert");
    existingAlerts.forEach((alert) => alert.remove());

    const alert = document.createElement("div");
    alert.className = `alert alert-${type} alert-dismissible fade show mt-3`;
    alert.innerHTML = `
        ${message}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    `;

    // Insertar la alerta al principio del contenedor
    targetContainer.insertBefore(alert, targetContainer.firstChild);

    setTimeout(() => {
        if (alert.parentNode) {
            alert.remove();
        }
    }, 5000);
}

function logout() {
    localStorage.removeItem("farmalineUser");
    sessionStorage.removeItem("farmalineUser");
    localStorage.removeItem("farmalineUserId");
    localStorage.removeItem("farmalineAdminId"); // También eliminar la sesión de administrador
    currentUser = null;
    window.location.href = "Login.html";
}

window.farmalineAuth = {
    logout,
    getCurrentUser: () => currentUser,
    isLoggedIn: () => currentUser !== null || localStorage.getItem("farmalineAdminId") !== null, // Incluir admin en isLoggedIn
};

function isEmail(email) {
    return /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,6}$/.test(email);
}