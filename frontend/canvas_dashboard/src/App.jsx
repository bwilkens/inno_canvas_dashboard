import { BrowserRouter, Routes, Route, Link } from 'react-router-dom';
import './css/navbar.css';
import './css/header.css';
import CourseOverview from './pages/course-overview.jsx';
import NotFoundPage from './pages/not-found-page.jsx';
import HuLogo from './assets/hu-logo-transparent.png';

function Home() {
    return <h1>Home Page</h1>;
}
var pageTitle = "Voortgang";

function App() {
  return (
    <BrowserRouter>
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

    <header id="page-header">
        <img src={HuLogo} alt="" />
        <h2>{pageTitle}</h2>
    </header>

      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/course-overview" element={<CourseOverview />} />
        <Route path="*" element={<NotFoundPage />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App