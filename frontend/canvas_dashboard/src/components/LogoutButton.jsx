const VITE_LOGOUT_URL = import.meta.env.VITE_LOGOUT_URL;

function LogoutButton() {
    const handleLogout = (event) => {
        event.preventDefault();
        window.location.href = VITE_LOGOUT_URL;
    };

    return (
        <a href={VITE_LOGOUT_URL} onClick={handleLogout}>
            Uitloggen
        </a>
    );
}

export default LogoutButton;
