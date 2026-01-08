import { BrowserRouter, Routes, Route } from 'react-router-dom';
import { useState, useEffect } from 'react';
import { getUserData } from './api/getUserData.js';
import { ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import './css/navbar.css';
import './css/header.css';
import CourseOverview from './pages/course-overview.jsx';
import NotFoundPage from './pages/not-found-page.jsx';
import DashboardPage from './pages/dashboard.jsx';
import AdminPortal from './pages/admin-portal.jsx';
import NavBar from './components/NavBar.jsx';
import HomePage from './pages/home.jsx';

const FIVE_SECONDS = 5000;

function App() {
    const [userRole, setUserRole] = useState('');
    const [userEmail, setUserEmail] = useState('');

    useEffect(() => {
        async function loadUser() {
            const data = await getUserData();
            setUserRole(data.appRole);
            setUserEmail(data.email);
        }
        loadUser();
    }, []);

    return (
        <BrowserRouter>
            <NavBar userRole={userRole} />
            <ToastContainer position="top-right" autoClose={FIVE_SECONDS} />

            <Routes>
                <Route path="/" element={<HomePage userRole={userRole} />} />
                <Route path="/course-overview" element={<CourseOverview />} />
                <Route
                    path="/admin-portal"
                    element={<AdminPortal userRole={userRole} userEmail={userEmail} />}
                />
                <Route
                    path="/dashboard/:instanceName"
                    element={<DashboardPage userRole={userRole} userEmail={userEmail} />}
                />
                <Route path="*" element={<NotFoundPage />} />
            </Routes>
        </BrowserRouter>
    );
}

export default App;
