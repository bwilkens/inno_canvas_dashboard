```mermaid
flowchart TD
    subgraph Presentatielaag [presentation]
        A[Controller]
    end
    subgraph Applicatielaag [application]
        B[Service]
    end
    subgraph Domeinlaag [domain]
        C[Domain]
    end
    subgraph Datalaag [data]
        D[Repository]
    end

    A --> B
    B --> C
    B --> D
```