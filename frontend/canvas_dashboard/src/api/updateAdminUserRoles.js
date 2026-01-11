const UNAUTHORIZED_STATUS = 401;
const FORBIDDEN_STATUS = 403;
const SERVER_ERROR_STATUS = 500;

export async function updateAdminUserRoles(users) {
    const response = await fetch('/api/v1/dashboard/users/admin', {
        method: 'PUT',
        credentials: 'include',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(users),
    });

    if (!response.ok) {
        let errorMessage;

        switch (response.status) {
            case UNAUTHORIZED_STATUS:
                errorMessage = 'Niet geautoriseerd: log opnieuw in om de rollen van users te veranderen.';
                break;
            case FORBIDDEN_STATUS:
                errorMessage = 'Toegang geweigerd: je hebt geen rechten om de rollen van users te veranderen.';
                break;
            case SERVER_ERROR_STATUS:
                errorMessage = 'Er is een serverfout opgetreden; de rollen van users zijn niet veranderd.';
                break;
            default:
                errorMessage = 'Er is iets misgegaan; de rollen van users zijn niet veranderd.';
        }

        throw new Error(errorMessage);
    }
}
