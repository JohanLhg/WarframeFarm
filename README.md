![Banner](https://user-images.githubusercontent.com/100712026/156604979-e686e0bd-f968-4a1c-b6aa-9e38d640e97c.png)

A companion app for Digital Extremes' *Warframe* to farm Primes

# The game
## Game description
*Warframe* is an online free-to-play third person shooter game developed by Digital Extremes. The goal is to complete missions scattered through the Solar System (called Origin System) to get ressources and unlock new equipments to level up.

## In-game elements used in this app
- Primes : upgraded verions of equipments (Warframes (armors), weapons, sentinels...).
- Components : parts of Primes required to build Primes (blueprint, blade, handle, receiver, barrel, chassis...).
- Relics : chest-like items, relics each have a set of 6 rewards ranging from common to rare (3 common, 2 uncommon, 1 rare). Opening one will grant you one of its rewards. Relics come in 4 different "eras" Lith, Meso, Neo and Axi defining their level (lowest level to highest). When new Primes come out, new relics come out with them (with the components for each new Prime) and older relics are placed in the "Prime Vault" and are removed from missions.
- Missions : each mission has a reward table, with each reward having a drop chance. Relics are a possible reward in missions (the higher the level of the mission, the higher the relic's era is going to be).

# The application
## Usage
One way to level up is to build and level up equipments, so to get to the max level it's important to get every Prime, but with 100+ Primes, 400+ relics and 270+ missions it's really difficult to keep count of what Prime components you still need, which relics to open and which missions you need to get those. That's why I decided to create this app, it works kinda like a check list but with additional functionalities.

## Installation
To install this app, you can follow these steps:
1. Install Android Studio
2. Get project from Version Control

![New Project](https://user-images.githubusercontent.com/100712026/158078347-996bd4fa-a50c-456d-9acf-60f88b128278.png)

3. Enter the project's URL and click **Clone**

![GitHub URL](https://user-images.githubusercontent.com/100712026/158078354-8d7ac001-3dc4-468d-8a6e-9038c55ea4b7.JPG)

Or download the project and move it to the **AndroidStudioProjects** folder.

## Presentation
### Lists
The app has lists of each element (Primes, components, relics, planets) with filters and search bars to easily find what you need. You can check each Prime and component you already own, the ones you don't have yet (for relics : the ones that contain rewards you don't have yet) will be shown first in lists by default.
| ![primes](https://user-images.githubusercontent.com/100712026/156620297-ab310ea2-c7f1-400d-9439-8a028a667565.jpg) | ![components](https://user-images.githubusercontent.com/100712026/156620224-cd61c9ac-4044-4339-8512-7c384ad9c712.jpg) | ![relics](https://user-images.githubusercontent.com/100712026/156620325-364b96fa-9115-4ef2-b21e-c9b2a76e28fb.jpg) | ![planets](https://user-images.githubusercontent.com/100712026/156620280-97aec7e7-2563-439a-b0a6-60e698b5a357.jpg) |
|---|---|---|---|

### Detail pages
Each element also has a detail page accessed by clicking on it. The detail pages show more information on the element :
- Prime : a list of its components, of the relics that contain them and of the missions to get them.
- Component : a list of the relics that contain it and of the missions to get them.
- Relic : a list of its rewards and of the missions to get the relic.
- Planet : a list of its missions.
- Mission : a list of the relics in its reward pool.

| ![prime](https://user-images.githubusercontent.com/100712026/156624420-5a190b46-a372-4bb1-80cc-7881d7c674cf.jpg) | ![partie](https://user-images.githubusercontent.com/100712026/156624334-eb3c3497-efa6-416a-b915-aff428fc4e55.jpg) | ![relique](https://user-images.githubusercontent.com/100712026/156624450-26cce354-c50b-443f-81e3-84dfe8698f1a.jpg) | ![mission](https://user-images.githubusercontent.com/100712026/156624314-bb40c35c-f909-4c2b-9503-0ec2e3788fb8.jpg) | ![planete](https://user-images.githubusercontent.com/100712026/156624388-6d8700fd-5d9f-42e8-8ed7-c59fde39a551.jpg) |
|---|---|---|---|---|


### Farm
The app also has a utility page where you can select the different Primes and components you need (all the Primes and components not owned are selected by default) and see which relics contain them and which missions you need to complete to get those relics.
<img src="https://user-images.githubusercontent.com/100712026/156622375-b4ef8b5b-36d3-47d4-9970-9a542f126f41.jpg" width="250">

## Used ressources
The app design is heavily inspired from the game and the official *Warframe Companion* app. Most of the images and some icons are from the game.  
Most of the icons used in this app are from [Flaticon](https://flaticon.com), authors:
- [Freepik](https://www.freepik.com)
- [Kiranshastry](https://www.flaticon.com/authors/kiranshastry)
- [Pixel Perfect](https://www.flaticon.com/authors/pixel-perfect)
