import { BrowserRouter, Routes, Route, Link } from "react-router-dom";
import { useState, useEffect } from "react";
import { getUserData } from "./api/getUserData.js";
import "./css/navbar.css";
import "./css/header.css";
import CourseOverview from "./pages/course-overview.jsx";
import NotFoundPage from "./pages/not-found-page.jsx";
import DashboardPage from "./pages/dashboard.jsx";
import AdminPortal from "./pages/admin-portal.jsx";
import NavBar from "./components/NavBar.jsx";

function Home() {
  return <h1>Home Page</h1>;
}

function App() {
  const [userRole, setUserRole] = useState("");
  const [userEmail, setUserEmail] = useState("");

  useEffect(() => {
    async function loadUser() {
      const data = await getUserData();
      setUserRole(data.role);
      setUserEmail(data.email);
    }
    loadUser();
  }, []);

  return (
    <BrowserRouter>
      <NavBar userRole={userRole} />

      <Routes>
        <Route path="/" element={<Home />} />
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
