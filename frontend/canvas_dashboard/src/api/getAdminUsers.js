const UNAUTHORIZED_STATUS = 401;
const FORBIDDEN_STATUS = 403;
const SERVER_ERROR_STATUS = 500;

export async function getAdminUsers() {
    const response = await fetch('/api/v1/dashboard/users/admin', {
        method: 'GET',
        credentials: 'include'
    });

    if (!response.ok) {
        let errorMessage;

        switch (response.status) {
            case UNAUTHORIZED_STATUS:
                errorMessage = 'Niet geautoriseerd: log opnieuw in om de lijst van admin users te bekijken.';
                break;
            case FORBIDDEN_STATUS:
                errorMessage = 'Toegang geweigerd: je hebt geen rechten om de lijst van admin users te bekijken.';
                break;
            case SERVER_ERROR_STATUS:
                errorMessage = 'Er is een serverfout opgetreden; de lijst van admin users kon niet worden opgehaald.';
                break;
            default:
                errorMessage = 'Er is iets misgegaan; de lijst van admin users kon niet worden opgehaald.';
        }

        throw new Error(errorMessage);
    }

    return await response.json();
}
