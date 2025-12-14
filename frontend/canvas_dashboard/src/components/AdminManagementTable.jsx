function AdminManagementTable({
    adminUsers,
    adminLoading,
    adminError,
}) {
    return (
        <div className="admin-management-group">
            <h2>Beheer Gebruikers</h2>
            {adminLoading && <div>Loading admin users...</div>}
            {adminError && <div>Error: {adminError}</div>}
            {!adminLoading && !adminError && (
                <table>
                    <thead>
                        <tr>
                            <th>Naam</th>
                            <th>Email</th>
                            <th>Rol</th>
                        </tr>
                    </thead>
                    <tbody>
                        {adminUsers.map((user) => (
                            <tr key={user.email}>
                                <td>{user.name}</td>
                                <td>{user.email}</td>
                                <td>{user.role}</td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            )}
        </div>
    );
}

export default AdminManagementTable;
