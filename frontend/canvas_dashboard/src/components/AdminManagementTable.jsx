import { useState, useEffect } from 'react';
import { updateAdminUsers } from '../api/updateAdminUsers.js';
import { getAdminUsers } from '../api/getAdminUsers.js';
import { toast } from 'react-toastify';
import '../css/admin-management.css';

const ROLE_OPTIONS = ['USER', 'ADMIN', 'SUPERADMIN'];

function AdminManagementTable() {
    const [adminUsers, setAdminUsers] = useState([]);
    const [adminLoading, setAdminLoading] = useState(true);
    const [adminError, setAdminError] = useState(null);

    const [editedUsers, setEditedUsers] = useState({});
    const [localUsers, setLocalUsers] = useState([]);
    const [saving, setSaving] = useState(false);

    useEffect(() => {
        fetchUsers();
    }, []);

    async function fetchUsers() {
        setAdminLoading(true);
        setAdminError(null);
        try {
            const data = await getAdminUsers();
            
            // sort users first by role (SUPERADMIN > ADMIN > USER) then by name
            const roleOrder = { SUPERADMIN: 0, ADMIN: 1, USER: 2 };
            const sorted = [...data].sort((a, b) => {
                if (roleOrder[a.appRole] !== roleOrder[b.appRole]) {
                    return roleOrder[a.appRole] - roleOrder[b.appRole];
                }
                return a.name.localeCompare(b.name);
            });
            setAdminUsers(sorted);
            setLocalUsers(sorted);
            setEditedUsers({});
        } catch (err) {
            setAdminError(err.message);
        } finally {
            setAdminLoading(false);
        }
    }

    const handleRoleChange = (email, newAppRole) => {
        setLocalUsers((prev) =>
            prev.map((user) =>
                user.email === email ? { ...user, appRole: newAppRole } : user
            )
        );
        setEditedUsers((prev) => ({
            ...prev,
            [email]: newAppRole,
        }));
    };

    const handleSave = async () => {
        const changed = localUsers.filter(
            (user) => isUserChanged(user, adminUsers, editedUsers)
        );
        if (changed.length === 0) {
            return;
        }
        setSaving(true);
        try {
            await updateAdminUsers(changed);

            // renew user list after updating roles
            await fetchUsers();
            toast.success('Wijzigingen succesvol opgeslagen!');
        } catch (err) {
            toast.error('Fout bij opslaan: ' + err.message);
        } finally {
            setSaving(false);
        }
    };

    function isUserChanged(user, adminUsers, editedUsers) {
        if (!editedUsers[user.email]) {
            return false;
        }
        const originalUser = adminUsers.find(u => u.email === user.email);
        return originalUser && user.appRole !== originalUser.appRole;
    }

    return (
        <div className="admin-management-group">
            <h2>Beheer Admins</h2>
            <div className="admin-management-table-wrapper">
                <div className='admin-management-table-content'>
                    {adminLoading && <div>Loading admin users...</div>}
                    {adminError && <div>Error: {adminError}</div>}
                    {!adminLoading && !adminError && (
                        <>
                            <table 
                                aria-label="Admin gebruikers tabel">
                                <thead>
                                    <tr>
                                        <th>Naam</th>
                                        <th>Email</th>
                                        <th>Rol</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {localUsers.map((user) => {
                                        const isSuperadmin = user.appRole === 'SUPERADMIN';
                                        const isChanged = isUserChanged(user, adminUsers, editedUsers);
                                        return (
                                            <tr
                                            key={user.email}
                                            className={
                                                (isChanged ? 'row-changed ' : '') +
                                                (isSuperadmin ? 'row-superadmin' : '')
                                            }
                                            >
                                                <td>{user.name}</td>
                                                <td>{user.email}</td>
                                                <td>
                                                    <select
                                                        aria-label={`Rol voor ${user.name}`}
                                                        value={user.appRole}
                                                        disabled={isSuperadmin}
                                                        onChange={(e) =>
                                                            handleRoleChange(
                                                                user.email,
                                                                e.target.value
                                                            )
                                                        }
                                                        >
                                                        {ROLE_OPTIONS.filter(
                                                            (role) =>
                                                                role !== 'SUPERADMIN' ||
                                                            user.appRole === 'SUPERADMIN'
                                                        ).map((role) => (
                                                            <option key={role} value={role}>
                                                                {role}
                                                            </option>
                                                        ))}
                                                    </select>
                                                </td>
                                            </tr>
                                        );
                                    })}
                                </tbody>
                            </table>
                            <button
                                onClick={handleSave}
                                disabled={
                                    localUsers.filter(user => isUserChanged(user, adminUsers, editedUsers)).length === 0
                                    || saving
                                }
                                className="save-button"
                                >
                                {saving ? 'Opslaan...' : 'Opslaan'}
                            </button>
                        </>
                    )}
                </div>
            </div>
        </div>
    );
}

export default AdminManagementTable;
