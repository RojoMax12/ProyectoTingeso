import ReactDOM from 'react-dom/client'
import './index.css'
import App from './App.jsx'
import { ReactKeycloakProvider } from "@react-keycloak/web";
import keycloak from "./Services/Keycloak";

ReactDOM.createRoot(document.getElementById('root')).render(
  <ReactKeycloakProvider authClient={keycloak}>
    <App />
  </ReactKeycloakProvider>
)

