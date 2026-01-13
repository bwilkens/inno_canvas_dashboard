import { redirectToLogin } from '../hooks/useAuthCheck';
const LogoutUrl = import.meta.env.VITE_LOGOUT_URL;

function LoginLogoutButton({ userRole }) {
    const isLoggedIn = !!userRole;

    const handleClick = (event) => {
        event.preventDefault();
        if (isLoggedIn) {
            window.location.href = LogoutUrl;
        } else {
            redirectToLogin();
        }
    };

    return (
        <a
            href="#"
            onClick={handleClick}
            className={isLoggedIn ? 'logout' : 'login'}
        >
            {isLoggedIn ? 'Uitloggen' : 'Inloggen'}
        </a>
    );
}

export default LoginLogoutButton;
