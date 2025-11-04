import { BrowserRouter, Routes, Route, Link } from 'react-router-dom';
import './css/navbar.css';
import CursusOverview from './pages/cursus-overview.jsx';
import NotFoundPage from './pages/not-found-page.jsx';

function Home() {
    return <h1>Home Page</h1>;
}

function App() {
  return (
    <BrowserRouter>
      <nav id="navbar">
        <ul>
          <li>
            <Link to="/">Home</Link>
          </li>
          <li>
            <Link to="/cursus-overview">Dashboard</Link>
          </li>
        </ul>
      </nav>

      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/cursus-overview" element={<CursusOverview />} />
        <Route path="*" element={<NotFoundPage />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App