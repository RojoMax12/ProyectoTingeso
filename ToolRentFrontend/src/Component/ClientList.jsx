import { useState, useEffect } from 'react';
import { Box, Typography, Paper, Stack, IconButton, Drawer, List, ListItem, ListItemButton, ListItemIcon, ListItemText, Button } from "@mui/material";
import { useNavigate } from 'react-router-dom';
import HomeIcon from "@mui/icons-material/Home";
import BuildIcon from "@mui/icons-material/Build";
import MenuIcon from "@mui/icons-material/Menu";
import ClientServices from '../Services/ClientServices';
import { useKeycloak } from '@react-keycloak/web';
import LibraryAddIcon from "@mui/icons-material/LibraryAdd"; // 📚➕ Para Agregar Herramienta
import AssessmentIcon from "@mui/icons-material/Assessment"; // 📊 Para Ver Kardex (reporte
import ContactsIcon from "@mui/icons-material/Contacts";     // 📇 Para Clientes)
import PersonAddAltIcon from '@mui/icons-material/PersonAddAlt';
import AdminPanelSettingsIcon from "@mui/icons-material/AdminPanelSettings"; // 🛡️⚙️ Para Configuraciones (admin)
import ReportIcon from '@mui/icons-material/Report'; // 📈 Para Reportes

const ClientList = () => {
    const [clients, setClients] = useState([]);
    const [drawerOpen, setDrawerOpen] = useState(false);
    const navigate = useNavigate();
    const { keycloak } = useKeycloak();
    const isAdmin = keycloak?.tokenParsed?.realm_access?.roles?.includes("ADMIN");
    

    const sidebarOptions = [
  { text: "Inicio", icon: <HomeIcon />, path: "/" },
  { text: "Herramientas", icon: <BuildIcon />, path: "/ToolList" },
  { text: "Agregar Herramienta", icon: <LibraryAddIcon />, path: "/AddTools" },
  { text: "Ver Kardex", icon: <AssessmentIcon />, path: "/Kardex" },
  { text: "Registrar Cliente", icon: <PersonAddAltIcon />, path: "/RegisterClient" },
  { text: "Clientes", icon: <ContactsIcon />, path: "/ClientList" },
  { text: "Reportes", icon: <ReportIcon />, path: "/Reports" },
  // Solo mostrar Configuraciones si es admin
  ...(isAdmin ? [{ text: "Configuraciones", icon: <AdminPanelSettingsIcon />, path: "/Configuration" }] : [])
];

    useEffect(() => {
        ClientServices.getAll()
            .then(response => {
                setClients(response.data);
            })
            .catch(error => {
                console.error("Error fetching clients:", error);
            });
    }, []);

    const handleEdit = (id) => {
        navigate(`/EditClient/${id}`);
    };

    const handleDelete = (id) => {
        // Implementa la lógica de borrado aquí
        ClientServices.deleteClient(id)
            .then(() => {
                setClients(clients.filter(client => client.id !== id));
            })
            .catch(error => {
                console.error("Error deleting client:", error);
            });
    };

    return (
        <>
            {/* Botón para abrir la barra lateral */}
            <IconButton
                color="inherit"
                onClick={() => setDrawerOpen(true)}
                sx={{ position: "fixed", top: 16, left: 16, zIndex: 10, backgroundColor: "#FA812F", boxShadow: 3 , '&:hover': { backgroundColor: "#FA812F" }}}
            >
                <MenuIcon />
            </IconButton>

            {/* Barra lateral temporal */}
            <Drawer
                open={drawerOpen}
                onClose={() => setDrawerOpen(false)}
                variant="temporary"
                sx={{
                    [`& .MuiDrawer-paper`]: { width: 220, boxSizing: "border-box", backgroundColor: "#FEF3E2" }
                }}
            >
                <List>
                    {sidebarOptions.map((option) => (
                        <ListItem key={option.text} disablePadding>
                            <ListItemButton onClick={() => { navigate(option.path); setDrawerOpen(false); }}>
                                <ListItemIcon sx={{ color: "#FA812F" }}>{option.icon}</ListItemIcon>
                                <ListItemText primary={option.text} />
                            </ListItemButton>
                        </ListItem>
                    ))}
                </List>
            </Drawer>

            {/* Fondo que cubre toda la pantalla */}
            <Box
                sx={{
                    position: "fixed",
                    top: 0,
                    left: 0,
                    width: "100vw",
                    height: "100vh",
                    backgroundColor: "#FEF3E2",
                    zIndex: -1
                }}
            />

            {/* Contenido principal con cuadro destacado */}
            <Box sx={{
                minHeight: "100vh",
                backgroundColor: "#FEF3E2",
                py: 4,
                display: "flex",
                alignItems: "center"
            }}>
                <Box
                    sx={{
                        maxWidth: 900,
                        mx: "auto",
                        mt: 2,
                        backgroundColor: "#fff8f0",
                        borderRadius: 4,
                        boxShadow: 6,
                        p: 4,
                        border: "2px solid rgba(255, 94, 0, 0.2)"
                    }}
                >
                    <Typography variant="h4" align="center" gutterBottom sx={{ fontWeight: "bold", mb: 4, color: "rgba(255, 94, 0, 1)" }}>
                        Lista de Clientes
                    </Typography>
                    <Paper sx={{ p: 2, boxShadow: 3 }}>
                        {clients.length === 0 ? (
                            <Typography align="center" color="text.secondary">
                                No hay clientes registrados.
                            </Typography>
                        ) : (
                            <Stack spacing={2}>
                                {clients.map(client => (
                                    <Box key={client.id} sx={{ display: "flex", justifyContent: "space-between", alignItems: "center", backgroundColor: "#fff", p: 2, borderRadius: 2 }}>
                                        <Box>
                                            <Typography variant="subtitle1" sx={{ fontWeight: "bold" }}>{client.name}</Typography>
                                            <Typography variant="body2" color="text.secondary">RUT: {client.rut}</Typography>
                                            <Typography variant="body2" color="text.secondary">Email: {client.email}</Typography>
                                            <Typography variant="body2" color="text.secondary">Teléfono: {client.phone}</Typography>
                                        </Box>
                                        <Box>
                                            <Button variant="outlined" color="primary" size="small" sx={{ mr: 1 }} onClick={() => handleEdit(client.id)}>
                                                Editar
                                            </Button>
                                            <Button variant="outlined" color="error" size="small" onClick={() => handleDelete(client.id)}>
                                                Eliminar
                                            </Button>
                                        </Box>
                                    </Box>
                                ))}
                            </Stack>
                        )}
                    </Paper>
                </Box>
            </Box>
        </>
    );
};

export default ClientList;