const UNAUTHORIZED_STATUS = 401;
const FORBIDDEN_STATUS = 403;
const SERVER_ERROR_STATUS = 500;

export async function updateDatabase() {
    const response = await fetch('/api/v1/dashboard/users/refresh', {
        method: 'POST',
        credentials: 'include',
    });

    if (!response.ok) {
        let errorMessage;

        switch (response.status) {
            case UNAUTHORIZED_STATUS:
                errorMessage = 'Niet geautoriseerd: log opnieuw in om de database te updaten.';
                break;
            case FORBIDDEN_STATUS:
                errorMessage = 'Toegang geweigerd: je hebt geen rechten om de database te updaten.';
                break;
            case SERVER_ERROR_STATUS:
                errorMessage = 'Er is een serverfout opgetreden; de database is niet geüpdatet.';
                break;
            default:
                errorMessage = 'Er is iets misgegaan; de database is niet geüpdatet.';
        }

        throw new Error(errorMessage);
    }
}
