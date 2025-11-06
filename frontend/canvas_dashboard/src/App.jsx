import { BrowserRouter, Routes, Route, Link } from 'react-router-dom';

function Home() {
    return <h1>Home Page</h1>;
}

function Dashboard() {
    return <h1>Dashboard Page</h1>;
}

function NotFoundPage() {
    return (
        <div>
            <h1>404 - Page Not Found</h1>
            <Link to="/">
                <button>Go back to Home</button>
            </Link>
        </div>
    );
}

function App() {
    return (
        <BrowserRouter>
            <nav>
                <Link to="/">Home</Link>
                <Link to="/dashboard">Dashboard</Link>
            </nav>

            <Routes>
                <Route path="/" element={<Home />} />
                <Route path="/dashboard" element={<Dashboard />} />
                <Route path="*" element={<NotFoundPage />} />
            </Routes>
        </BrowserRouter>
    )
}

export default App