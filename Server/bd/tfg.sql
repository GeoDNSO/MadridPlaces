-- phpMyAdmin SQL Dump
-- version 5.0.3
-- https://www.phpmyadmin.net/
--
-- Servidor: 127.0.0.1
-- Tiempo de generación: 07-12-2020 a las 17:04:25
-- Versión del servidor: 10.4.14-MariaDB
-- Versión de PHP: 7.2.34

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `tfg`
--
CREATE DATABASE IF NOT EXISTS `tfg` DEFAULT CHARACTER SET utf8 COLLATE utf8_spanish_ci;
USE `tfg`;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `location`
--

CREATE TABLE `location` (
  `name` varchar(255) COLLATE utf8_spanish_ci NOT NULL,
  `description` varchar(255) COLLATE utf8_spanish_ci NOT NULL,
  `direction` varchar(255) COLLATE utf8_spanish_ci NOT NULL,
  `coordinate_latitude` double NOT NULL,
  `coordinate_longitude` double NOT NULL,
  `picture` varchar(255) COLLATE utf8_spanish_ci NOT NULL,
  `type_of_place` varchar(255) COLLATE utf8_spanish_ci NOT NULL,
  `city` varchar(255) COLLATE utf8_spanish_ci NOT NULL DEFAULT 'Madrid',
  `affluence` varchar(255) COLLATE utf8_spanish_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_spanish_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `user`
--

CREATE TABLE `user` (
  `nickname` varchar(255) COLLATE utf8_spanish_ci NOT NULL,
  `name` varchar(255) COLLATE utf8_spanish_ci NOT NULL,
  `surname` varchar(255) COLLATE utf8_spanish_ci NOT NULL,
  `email` varchar(255) COLLATE utf8_spanish_ci NOT NULL,
  `password` varchar(255) COLLATE utf8_spanish_ci NOT NULL,
  `gender` varchar(50) COLLATE utf8_spanish_ci NOT NULL,
  `birth_date` date NOT NULL,
  `city` varchar(255) COLLATE utf8_spanish_ci NOT NULL DEFAULT 'Madrid',
  `rol` varchar(255) COLLATE utf8_spanish_ci NOT NULL DEFAULT 'user'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_spanish_ci;

--
-- Volcado de datos para la tabla `user`
--

INSERT INTO `user` (`nickname`, `name`, `surname`, `email`, `password`, `gender`, `birth_date`, `city`, `rol`) VALUES
('El pepe', 'juanAn', 'Esco', 'asdasd', '1234', 'nosesabe', '1998-11-20', 'Madrid', 'user'),
('pepe', 'juanAn', 'Esco', 'asdasd', '1234', 'nosesabe', '1998-11-20', 'Madrid', 'user'),
('poti4', 'juanAn', 'Esco', 'asdasd', '1234', 'nosesabe', '1998-11-20', 'Madrid', 'user'),
('poti5', 'juanAn', 'Esco', 'asdasd', '1234', 'nosesabe', '1998-11-20', 'Madrid', 'user');

--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `location`
--
ALTER TABLE `location`
  ADD PRIMARY KEY (`name`);

--
-- Indices de la tabla `user`
--
ALTER TABLE `user`
  ADD PRIMARY KEY (`nickname`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
