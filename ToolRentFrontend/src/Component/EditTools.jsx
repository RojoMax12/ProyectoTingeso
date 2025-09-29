import React, { useState } from "react";
import { Box, Typography, Paper, TextField, Button, Stack, IconButton } from "@mui/material";
import { useNavigate } from "react-router";
import MenuIcon from "@mui/icons-material/Menu";
import { Drawer, List, ListItem, ListItemButton, ListItemIcon, ListItemText } from "@mui/material";
import ToolServices from "../Services/ToolServices";
import HomeIcon from "@mui/icons-material/Home";
import BuildIcon from "@mui/icons-material/Build";
import AddBoxIcon from "@mui/icons-material/AddBox";
import { useParams } from "react-router-dom";
import { useKeycloak } from "@react-keycloak/web";
import LibraryAddIcon from "@mui/icons-material/LibraryAdd";
import AssessmentIcon from "@mui/icons-material/Assessment";
import ContactsIcon from "@mui/icons-material/Contacts";
import PersonAddAltIcon from '@mui/icons-material/PersonAddAlt';
import AdminPanelSettingsIcon from "@mui/icons-material/AdminPanelSettings";
import ReportIcon from '@mui/icons-material/Report';


const EditTools = () => {
    const [toolName, setToolName] = useState("");
    const [toolCategory, setToolCategory] = useState("");
    const [toolRemplacementCost, setToolRemplacementCost] = useState("");
    const [drawerOpen, setDrawerOpen] = useState(false);
    const navigate = useNavigate();
    const {id} = useParams(); // <-- Obtiene el id de la URL

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


    const handleSave = () => {
        alert("Herramienta actualizada");
        ToolServices.update({
            id: id,
            name: toolName,
            category: toolCategory,
            replacement_cost: toolRemplacementCost
        })
            .then(() => {
                alert("Herramienta actualizada");
                navigate("/ToolList");
            })
            .catch((error) => {
                console.error("There was an error!", error);
                alert("Error al actualizar la herramienta");
            });
    };

    return (
        <>
            <IconButton
                color="primary"
                onClick={() => setDrawerOpen(true)}
                sx={{ position: "fixed", top: 16, left: 16, zIndex: 10, backgroundColor: "#FA812F", boxShadow: 3 , '&:hover': { backgroundColor: "#FA812F" }}}
            >
                <MenuIcon />
            </IconButton>
            <Drawer
                anchor="left"
                open={drawerOpen}
                onClose={() => setDrawerOpen(false)}
                variant="temporary"
                sx={{
                    ["& .MuiDrawer-paper"]: { width: 240, boxSizing: "border-box", backgroundColor: "#fff8f0" }
                }}
            >
                {/* Contenido de la barra lateral */}
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

        <Box sx={{ minHeight: "100vh", backgroundColor: "#f5f5f5", py: 4 }}>
            <Box sx={{ maxWidth: 500, mx: "auto", mt: 4 }}>
                <Typography variant="h4" align="center" gutterBottom sx={{ fontWeight: "bold", color: "rgba(255, 94, 0, 1)" }}>
                    Editar Herramienta
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
                            onChange={(e) => setToolCategory(e.target.value)}
                            fullWidth
                        />
                        <TextField
                            label="Costo de Reemplazo"
                            variant="outlined"
                            type="number"
                            value={toolRemplacementCost}
                            onChange={(e) => setToolRemplacementCost(e.target.value)}
                            fullWidth
                        />
                        <Button
                            variant="contained"
                            color="primary"
                            onClick={handleSave}
                            sx={{ backgroundColor: "rgba(255, 94, 0, 1)" }}
                        >
                            Guardar Cambios
                        </Button>
                        <Button
                            variant="outlined"
                            color="primary"
                            onClick={() => navigate("/ToolList")}
                        >
                            Cancelar
                        </Button>
                    </Stack>
                </Paper>
            </Box>
        </Box>
    </> 
    );
};

export default EditTools;