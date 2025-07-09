document.addEventListener("DOMContentLoaded", () => {
    const toggleSidebarBtn = document.getElementById("toggle-sidebar-btn");
    const sidebar = document.getElementById("admin-sidebar");
    const logoutBtn = document.getElementById("logout-btn");

    if (toggleSidebarBtn && sidebar) {
        toggleSidebarBtn.addEventListener("click", () => {
            sidebar.classList.toggle("active");
            document.body.classList.toggle("sidebar-open");
        });
    }

    document.body.addEventListener("click", (event) => {
        if (
            sidebar &&
            sidebar.classList.contains("active") &&
            !sidebar.contains(event.target) &&
            toggleSidebarBtn &&
            !toggleSidebarBtn.contains(event.target)
        ) {
            sidebar.classList.remove("active");
            document.body.classList.remove("sidebar-open");
        }
    });

    window.addEventListener("resize", () => {
        if (window.innerWidth > 992 && sidebar) {
            sidebar.classList.remove("active");
            document.body.classList.remove("sidebar-open");
        }
    });

    if (logoutBtn) {
        logoutBtn.addEventListener("click", (e) => {
            e.preventDefault();
            if (confirm("¿Está seguro que desea cerrar sesión?")) {
                localStorage.removeItem("farmalineAdminId");
                localStorage.removeItem("userData");
                localStorage.removeItem("userRole");

                sessionStorage.clear();

                window.location.href = "Login.html";
            }
        });
    }
});