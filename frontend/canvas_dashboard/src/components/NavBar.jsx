import { Link } from 'react-router-dom';
import '../css/navbar.css';
import LogoutButton from "../components/LogoutButton";

function NavBar() {
    return (
        <nav id="navbar">
            <ul>
                <li>
                    <Link to="/">Home</Link>
                </li>
                <li>
                    <Link to="/course-overview">Dashboard</Link>
                </li>
                <li>
                    <Link to="/token">Show Token</Link>
                </li>
                <li>
                    <LogoutButton />
                </li>
            </ul>
        </nav>
    );
}

export default NavBar;