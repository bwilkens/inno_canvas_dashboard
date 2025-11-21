export default function LogoutButton() {
    const handleLogout = () => {
        window.location.href = "http://localhost:8080/logout"; // TODO: should not be hardcoded
    };

    return (
        <button onClick={handleLogout}>
            Logout
        </button>
    );
}
