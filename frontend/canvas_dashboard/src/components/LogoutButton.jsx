
const LOGOUT_URL = import.meta.env.VITE_LOGOUT_URL;
import { redirectToLogin } from "../hooks/useAuthCheck";

function LogoutButton({ userRole }) {
  const isLoggedIn = !!userRole;

  const handleClick = (event) => {
    event.preventDefault();
    if (isLoggedIn) {
      window.location.href = LOGOUT_URL;
    } else {
      redirectToLogin();
    }
  };

  return (
    <a
      href="#"
      onClick={handleClick}
      className={isLoggedIn ? "logout" : "login"}
    >
      {isLoggedIn ? "Uitloggen" : "Inloggen"}
    </a>
  );
}

export default LogoutButton;