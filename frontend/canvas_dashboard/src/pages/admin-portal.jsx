import { useState, useEffect } from "react";
import { Navigate } from "react-router-dom";
import { getUserData } from "../api/getUserData.js";
import { getAdminUsers } from "../api/getAdminUsers.js";
import AdminActionButton from "../components/AdminActionButton";
import UserInfo from "../components/UserInformation";
import useAuthCheck from "../hooks/useAuthCheck";
import "../css/admin-button.css";
import AdminManagementTable from "../components/AdminManagementTable.jsx";

const AdminDashboard = () => {
  useAuthCheck();

  const [userData, setUserData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const [adminUsers, setAdminUsers] = useState([]);
  const [adminLoading, setAdminLoading] = useState(false);
  const [adminError, setAdminError] = useState(null);

  useEffect(() => {
    async function loadData() {
      try {
        const data = await getUserData();
        setUserData(data);
      } catch (error) {
        setError(error);
      } finally {
        setLoading(false);
      }
    }
    loadData();
  }, []);

  useEffect(() => {
    if (userData && userData.role === "SUPERADMIN") {
        setAdminLoading(true);
        async function loadAdmins() {
        try {
            const data = await getAdminUsers();

            // sort users by role first and then by name
            const sortedUsers = [...data].sort((a, b) => {
            if (a.role !== b.role) return a.role.localeCompare(b.role);
            return a.name.localeCompare(b.name);
            });
            setAdminUsers(sortedUsers);
        } catch (err) {
            setAdminError(err.message);
        } finally {
            setAdminLoading(false);
        }
        }
        loadAdmins();
    }
  }, [userData]);

  if (loading) return <div>Loading...</div>;
  if (error) return <div>Error loading user data.</div>;
  if (!userData) return <Navigate to="/" replace />;
  if (userData.role !== "ADMIN" && userData.role !== "SUPERADMIN") return <Navigate to="/" replace />;

  function handleHealth() {
    fetch("/api/health", {
      method: "POST",
      credentials: "include",
    })
      .then((response) => {
        if (!response.ok) {
          throw new Error("Health check failed");
        }
        return response.json();
      })
      .then((data) => {
        console.log("Health check successful:", data);
      })
      .catch((error) => {
        alert(error.message);
      });
  }
  function handleGenerateResult() {
    fetch("", {
      method: "POST",
      credentials: "include",
    })
      .then((response) => {
        if (!response.ok) {
          throw new Error("Generate result failed");
        }
        return response.json();
      })
      .catch((error) => {
        alert(error.message);
      });
  }
  function handleGenerateCourse() {
    fetch("", {
      method: "POST",
      credentials: "include",
    })
      .then((response) => {
        if (!response.ok) {
          throw new Error("Generate course failed");
        }
        return response.json();
      })
      .catch((error) => {
        alert(error.message);
      });
  }

  return (
    <div>
      <UserInfo data={userData} />
      <div className="admin-button-group">
        <AdminActionButton
          name="Genereer Resultaat"
          onClick={handleGenerateResult}
        ></AdminActionButton>

        <AdminActionButton
          name="Genereer Cursus"
          onClick={handleGenerateCourse}
        ></AdminActionButton>

        <AdminActionButton
          name="Update Cursus"
          disabled={true}
        ></AdminActionButton>

        <AdminActionButton
          name="Health"
          onClick={handleHealth}
        ></AdminActionButton>
      </div>
      {userData.role === "SUPERADMIN" && (
        <AdminManagementTable
            adminUsers={adminUsers}
            adminLoading={adminLoading}
            adminError={adminError}
        />
      )}
    </div>
  );
};

export default AdminDashboard;
