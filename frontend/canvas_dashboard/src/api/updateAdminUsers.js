export async function updateAdminUsers(users) {
    const response = await fetch('/api/v1/dashboard/users/admin', {
        method: 'PUT',
        credentials: 'include',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(users),
    });

    if (!response.ok) {
        throw new Error('Failed to update admin users');
    }

    return await response.json();
}
