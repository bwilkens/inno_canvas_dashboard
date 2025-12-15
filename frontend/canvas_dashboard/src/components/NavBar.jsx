import { Link } from "react-router-dom";
import "../css/navbar.css";
import LogoutButton from "../components/LogoutButton";
import HuLogo from "../assets/hu-logo.svg";

function NavBar({ userRole }) {
  return (
    <nav id="navbar">
      <ul>
        <li>
          <Link to="/">
            <img
              src={HuLogo}
              alt="Hogeschool Utrecht logo die terug wijst naar de startpagina."
            />
          </Link>
        </li>
        <li>
          <Link to="/course-overview">Cursus Overzicht</Link>
        </li>
        {(userRole === "ADMIN" || userRole === "SUPERADMIN") && (
          <li>
            <Link to="/admin-portal">Admin Portaal</Link>
          </li>
        )}
        <li>
          <LogoutButton />
        </li>
      </ul>
    </nav>
  );
}

export default NavBar;
