export async function getAdminUsers() {
    const response = await fetch('/api/v1/dashboard/users/admin', {
        method: 'GET',
        credentials: 'include'
    });

    if (!response.ok) {
        throw new Error('Could not fetch admin users');
    }

    return await response.json();
}
