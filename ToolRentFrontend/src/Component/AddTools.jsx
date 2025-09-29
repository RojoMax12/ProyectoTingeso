import { useState } from "react";
import ToolServices from "../Services/ToolServices";
import { useKeycloak } from "@react-keycloak/web";
import { Box, Typography, Paper, TextField, Button, Stack, Drawer, List, ListItem, ListItemButton, ListItemIcon, ListItemText, IconButton } from "@mui/material";
import { useNavigate } from "react-router";
import HomeIcon from "@mui/icons-material/Home";
import BuildIcon from "@mui/icons-material/Build";
import MenuIcon from "@mui/icons-material/Menu";
import kardexServices from "../Services/KardexServices";
import LibraryAddIcon from "@mui/icons-material/LibraryAdd"; // 📚➕ Para Agregar Herramienta
import AssessmentIcon from "@mui/icons-material/Assessment"; // 📊 Para Ver Kardex (reporte
import ContactsIcon from "@mui/icons-material/Contacts";     // 📇 Para Clientes)
import PersonAddAltIcon from '@mui/icons-material/PersonAddAlt';
import AdminPanelSettingsIcon from "@mui/icons-material/AdminPanelSettings"; // 🛡️⚙️ Para Configuraciones (admin)
import ReportIcon from '@mui/icons-material/Report'; // 📈 Para Reportes


const AddTools = () => {
    const { keycloak } = useKeycloak();
    const [toolName, setToolName] = useState("");
    const [toolCategory, setCategory] = useState("");
    const [toolRemplacementcost, setToolRemplacementcost] = useState("");
    const [tools, setTools] = useState([]);
    const [drawerOpen, setDrawerOpen] = useState(false);
    const [kardex, setKardex] = useState([]);
    const navigate = useNavigate();
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

    const handleAdd = () => {
        const toolEntity = {
            name: toolName,
            category: toolCategory,
            replacement_cost: toolRemplacementcost
        };

        ToolServices.create(toolEntity)
            .then((response) => {
                setTools([...tools, response.data]);
                setToolName("");
                setCategory("");
                setToolRemplacementcost("");
                kardexServices.create({
                    date: new Date(),
                    stateToolsId: 1,
                    username: keycloak.tokenParsed.preferred_username,
                    quantity: 1,
                    idtool: response.data.id
                });
                alert("Herramienta agregada exitosamente");
            })
            .catch((error) => {
                console.error("There was an error!", error);
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

            {/* Contenido principal */}
            // ...existing code...
            <Box sx={{ minHeight: "100vh", backgroundColor: "#FEF3E2", py: 4 }}>
                <Box
                    sx={{
                        maxWidth: 500,
                        mx: "auto",
                        mt: 4,
                        backgroundColor: "#fff8f0",
                        borderRadius: 4,
                        boxShadow: 6,
                        p: 4,
                        border: "2px solid rgba(255, 94, 0, 0.2)"
                    }}
                >
                    <Typography
                        variant="h4"
                        align="center"
                        gutterBottom
                        sx={{ fontWeight: "bold", color: "rgba(255, 94, 0, 1)" }}
                    >
                        Agregar Nueva Herramienta
                    </Typography>
                    <Paper sx={{ p: 4, boxShadow: 3, backgroundColor: "transparent", elevation: 0 }}>
                        <Stack spacing={3}>
                            <TextField
                                label="Nombre de la Herramienta"
                                variant="outlined"
                                value={toolName}
                                onChange={(e) => setToolName(e.target.value)}
                                fullWidth
                            />
                            <TextField
                                label="Categoría"
                                variant="outlined"
                                value={toolCategory}
                                onChange={(e) => setCategory(e.target.value)}
                                fullWidth
                            />
                            <TextField
                                label="Costo de Reemplazo"
                                variant="outlined"
                                type="number"
                                value={toolRemplacementcost}
                                onChange={(e) => setToolRemplacementcost(e.target.value)}
                                fullWidth
                            />
                            <Button
                                variant="contained"
                                color="primary"
                                onClick={handleAdd}
                                sx={{ backgroundColor: "rgba(255, 94, 0, 1)" }}
                            >
                                Agregar Herramienta
                            </Button>
                            <Button
                                variant="contained"
                                color="primary"
                                onClick={() => navigate("/ToolList")}
                                sx={{ backgroundColor: "rgba(255, 94, 0, 1)" }}
                            >
                                Ir a Lista de Herramientas
                            </Button>
                        </Stack>
                    </Paper>
                </Box>
            </Box>
            // ...existing code...
        </>
    );
};

export default AddTools;