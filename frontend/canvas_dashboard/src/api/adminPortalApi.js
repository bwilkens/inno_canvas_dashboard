const UNAUTHORIZED_STATUS = 401;
const FORBIDDEN_STATUS = 403;
const SERVER_ERROR_STATUS = 500;

export async function refreshCanvasData() {
    const response = await fetch('', {
        method: 'POST',
        credentials: 'include',
    });

    if (!response.ok) {
        let errorMessage;

        switch (response.status) {
            case UNAUTHORIZED_STATUS:
                errorMessage = 'Niet geautoriseerd: log opnieuw in om de Canvas-gegevens te verversen.';
                break;
            case FORBIDDEN_STATUS:
                errorMessage = 'Toegang geweigerd: je hebt geen rechten om de Canvas-gegevens te verversen.';
                break;
            case SERVER_ERROR_STATUS:
                errorMessage = 'Er is een serverfout opgetreden; de Canvas-gegevens zijn niet ververst.';
                break;
            default:
                errorMessage = 'Er is iets misgegaan; de Canvas-gegevens zijn niet ververst.';
        }

        throw new Error(errorMessage);
    }
}

export async function refreshDashboards() {
    const response = await fetch('', {
        method: 'POST',
        credentials: 'include',
    });

    if (!response.ok) {
        let errorMessage;

        switch (response.status) {
            case UNAUTHORIZED_STATUS:
                errorMessage = 'Niet geautoriseerd: log opnieuw in om de dashboards te vernieuwen.';
                break;
            case FORBIDDEN_STATUS:
                errorMessage = 'Toegang geweigerd: je hebt geen rechten om de dashboards te vernieuwen.';
                break;
            case SERVER_ERROR_STATUS:
                errorMessage = 'Er is een serverfout opgetreden; de dashboards zijn niet vernieuwd.';
                break;
            default:
                errorMessage = 'Er is iets misgegaan; de dashboards zijn niet vernieuwd.';
        }

        throw new Error(errorMessage);
    }
}

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
