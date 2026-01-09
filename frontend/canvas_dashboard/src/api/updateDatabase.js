export async function updateDatabase() {
    const response = await fetch('/api/v1/dashboard/users/refresh', {
        method: 'POST',
        credentials: 'include',
    });

    if (!response.ok) {
        throw new Error('Er is iets misgegaan; de database is niet geüpdatet.');
    }
}
