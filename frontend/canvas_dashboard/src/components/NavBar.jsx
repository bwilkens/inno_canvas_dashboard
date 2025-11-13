import { Link } from 'react-router-dom';
import '../css/navbar.css';

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
      </ul>
    </nav>
  );
}

export default NavBar;