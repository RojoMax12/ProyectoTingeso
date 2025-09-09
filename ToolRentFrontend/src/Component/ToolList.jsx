import { useState, useEffect } from "react";
import { TableContainer, Paper, Table, TableHead, TableRow, TableCell, TableBody, Button, Typography, Box, Drawer, List, ListItem, ListItemButton, ListItemIcon, ListItemText, IconButton } from "@mui/material";
import ToolServices from "../Services/ToolServices";
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

const ToolList = () => {
    const [tools, setTools] = useState([]);
    const [drawerOpen, setDrawerOpen] = useState(false); // Estado para mostrar/ocultar la barra lateral
    const navigate = useNavigate();

    const handleGoToAddTool = () => {
        navigate("/AddTools");
    };

    const handleEdit = (id) => {
        navigate(`/EditTools/${id}`);
    };

    const handleDelete = (id) => {
        if (window.confirm("¿Seguro que quieres eliminar esta herramienta?")) {
            ToolServices.deleteid(id)
                .then(() => {
                    setTools(tools.filter(tool => tool.id !== id));
                })
                .catch(error => {
                    console.error("Error al eliminar herramienta:", error);
                });
        }
    };

    useEffect(() => {
        ToolServices.getAll()
            .then((response) => {
                setTools(response.data);
            })
            .catch((error) => {
                console.error("There was an error!", error);
            });
    }, []);

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

            {/* Contenido principal sin margen izquierdo */}
            <Box sx={{ maxWidth: 900, mx: "auto", py: 2, mt: -50, position: "relative", zIndex: 1 }}>
                <Typography variant="h4" align="center" gutterBottom sx={{ fontWeight: "bold", mb: 4 , color: "rgba(255, 94, 0, 1)"}}>
                    Lista de Herramientas
                </Typography>
                <TableContainer component={Paper} sx={{ boxShadow: 3 }}>
                    <Table>
                        <TableHead>
                            <TableRow sx={{ backgroundColor: "rgba(255, 94, 0, 1)" }}>
                                <TableCell sx={{ color: "#fff" }}>ID</TableCell>
                                <TableCell sx={{ color: "#fff" }}>Nombre</TableCell>
                                <TableCell sx={{ color: "#fff" }}>Categoría</TableCell>
                                <TableCell sx={{ color: "#fff" }}>Costo Reemplazo</TableCell>
                                <TableCell sx={{ color: "#fff" }}>Acciones</TableCell>
                            </TableRow>
                        </TableHead>
                        <TableBody>
                            {tools.map((tool) => (
                                <TableRow key={tool.id}>
                                    <TableCell>{tool.id}</TableCell>
                                    <TableCell>{tool.name}</TableCell>
                                    <TableCell>{tool.category}</TableCell>
                                    <TableCell>{tool.replacement_cost}</TableCell>
                                    <TableCell>
                                        <Button
                                            variant="outlined"
                                            color="primary"
                                            size="small"
                                            sx={{ mr: 1 }}
                                            onClick={() => handleEdit(tool.id)}
                                        >
                                            Editar
                                        </Button>
                                        <Button
                                            variant="outlined"
                                            color="error"
                                            size="small"
                                            onClick={() => handleDelete(tool.id)}
                                        >
                                            Eliminar
                                        </Button>
                                    </TableCell>
                                </TableRow>
                            ))}
                        </TableBody>
                    </Table>
                </TableContainer>
                <Box sx={{ display: "flex", justifyContent: "flex-end", mt: 2 }}>
                    <Button variant="contained" color="primary" onClick={handleGoToAddTool} sx={{ backgroundColor: "rgba(255, 94, 0, 1)" }}>
                        Agregar Herramienta
                    </Button>
                </Box>
            </Box>
        </>
    );
};

export default ToolList;