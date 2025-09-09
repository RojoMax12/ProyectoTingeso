import React, { useState } from "react";
import ToolServices from "../Services/ToolServices";
import { useKeycloak } from "@react-keycloak/web";
import { Box, Typography, Paper, TextField, Button, Stack, Drawer, List, ListItem, ListItemButton, ListItemIcon, ListItemText, IconButton } from "@mui/material";
import { useNavigate } from "react-router";
import HomeIcon from "@mui/icons-material/Home";
import BuildIcon from "@mui/icons-material/Build";
import AddBoxIcon from "@mui/icons-material/AddBox";
import MenuIcon from "@mui/icons-material/Menu";

const sidebarOptions = [
    { text: "Inicio", icon: <HomeIcon />, path: "/" },
    { text: "Herramientas", icon: <BuildIcon />, path: "/ToolList" },
    { text: "Agregar Herramienta", icon: <AddBoxIcon />, path: "/AddTools" }
];

const AddTools = () => {
    const { keycloak } = useKeycloak();
    const [toolName, setToolName] = useState("");
    const [toolCategory, setCategory] = useState("");
    const [toolRemplacementcost, setToolRemplacementcost] = useState("");
    const [tools, setTools] = useState([]);
    const [drawerOpen, setDrawerOpen] = useState(false);
    const navigate = useNavigate();

    const handleAdd = () => {
        const toolEntity = {
            name: toolName,
            category: toolCategory,
            replacement_cost: toolRemplacementcost
        };

        const userEntity = {
            rut: keycloak.tokenParsed?.Rut,
            name: keycloak.tokenParsed?.name,
            email: keycloak.tokenParsed?.email,
            phone: keycloak.tokenParsed?.Phone_number,
            role: keycloak.tokenParsed?.roles,
            password: ""
        };

        ToolServices.create(toolEntity)
            .then((response) => {
                setTools([...tools, response.data]);
                setToolName("");
                setCategory("");
                setToolRemplacementcost("");
            })
            .catch((error) => {
                console.error("There was an error!", error);
            });
    };

    return (
        <>
            {/* Botón para abrir la barra lateral */}
            <IconButton
                color="primary"
                onClick={() => setDrawerOpen(true)}
                sx={{ position: "fixed", top: 16, left: 16, zIndex: 10 }}
            >
                <MenuIcon />
            </IconButton>

            {/* Barra lateral temporal */}
            <Drawer
                open={drawerOpen}
                onClose={() => setDrawerOpen(false)}
                variant="temporary"
                sx={{
                    [`& .MuiDrawer-paper`]: { width: 220, boxSizing: "border-box", backgroundColor: "#fff8f0" }
                }}
            >
                <List>
                    {sidebarOptions.map((option) => (
                        <ListItem key={option.text} disablePadding>
                            <ListItemButton onClick={() => { navigate(option.path); setDrawerOpen(false); }}>
                                <ListItemIcon>{option.icon}</ListItemIcon>
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
                    backgroundColor: "#f5f5f5",
                    zIndex: -1
                }}
            />

            {/* Contenido principal */}
            <Box sx={{ minHeight: "100vh", backgroundColor: "#f5f5f5", py: 4 }}>
                <Box sx={{ maxWidth: 500, mx: "auto", mt: 4 }}>
                    <Typography variant="h4" align="center" gutterBottom sx={{ fontWeight: "bold", color: "rgba(255, 94, 0, 1)" }}>
                        Agregar Nueva Herramienta
                    </Typography>
                    <Paper sx={{ p: 4, boxShadow: 3 }}>
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
                                variant="outlined"
                                color="primary"
                                onClick={() => navigate("/ToolList")}
                            >
                                Ir a Lista de Herramientas
                            </Button>
                        </Stack>
                    </Paper>
                </Box>
            </Box>
        </>
    );
};

export default AddTools;