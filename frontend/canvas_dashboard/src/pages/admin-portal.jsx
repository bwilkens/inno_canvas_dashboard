import { useState, useEffect } from 'react';
import { Navigate, useNavigate } from 'react-router-dom';
import { getUserData } from '../api/getUserData.js';
import { toast } from 'react-toastify';
import AdminActionButton from '../components/AdminActionButton';
import UserInfo from '../components/UserInformation';
import useAuthCheck from '../hooks/useAuthCheck';
import '../css/admin-button.css';
import AdminManagementTable from '../components/AdminManagementTable.jsx';

const AdminDashboard = () => {
    useAuthCheck();
    const navigate = useNavigate();

    const [userData, setUserData] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

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

    if (loading) {
        return <div>Loading...</div>;
    }
    if (error) {
        return <div>Error loading user data.</div>;
    }
    if (!userData) {
        return <Navigate to="/" replace />;
    }
    if (userData.appRole !== 'ADMIN' && userData.appRole !== 'SUPERADMIN') {
        return <Navigate to="/" replace />;
    }

    function handleHealth() {
        navigate('/health');
    }

    function handleGenerateResult() {
        fetch('', {
            method: 'POST',
            credentials: 'include',
        })
            .then((response) => {
                if (!response.ok) {
                    throw new Error('Generate result failed');
                }
                return response.json();
            })
            .catch((error) => {
                toast.error(error.message);
            });
    }
    function handleGenerateCourse() {
        fetch('', {
            method: 'POST',
            credentials: 'include',
        })
            .then((response) => {
                if (!response.ok) {
                    throw new Error('Generate course failed');
                }
                return response.json();
            })
            .catch((error) => {
                toast.error(error.message);
            });
    }

    return (
        <div>
            <h1>Admin Portaal</h1>
            <UserInfo data={userData} />

            <h2>Admin Acties</h2>
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
            {userData.appRole === 'SUPERADMIN' && (
                <AdminManagementTable />
            )}
        </div>
    );
};

export default AdminDashboard;
