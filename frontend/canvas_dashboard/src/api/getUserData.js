export async function getUserData() {
    const response = await fetch('/api/v1/dashboard/users', {
        method: 'GET',
        credentials: 'include'
    });

    if (!response.ok) {
        throw new Error('Could not fetch user data');
    }

    return await response.json();
}
