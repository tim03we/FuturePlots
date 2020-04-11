# FuturePlots

FuturePlots is a plot plugin that will be further developed in the future. This Plot Plugin should be an alternative to PlotSquared for Nukkit, because from Nukkit 2.0 on FastAsyncWorldEdit and PlotSquared will not work anymore.This Plugin have many Features:

# Commands
Command | Sub-Command | Permission | Alias
------- | ----------- | ---------- | ------
/p | addhelper | - | trust
/p | removehelper | - | rmhelper
~ | auto | - | -
~ | claim | - | c
~ | clear | - | reset
~ | delete | - | del
~ | help | - | -
~ | home | - | h
~ | homes | - | -
~ | info | - | i

# Config
```yaml
---
# Available languages: eng
lang: "eng"

# Available providers: yaml
provider: "yaml"

# How many plots a player can own
max-plots: 2

# Whether the popup should appear when you enter a plot
show-popup: true

# Here you find the plot configurations, how the plot world should look like later
# To generate the world, a world name must be specified
world:
  level: ""
  plotSize: 32
  roadBlock: 5
  wallBlock:  44
  bottomBlock: 7
  plotFloorBlock: 2
  plotFillBlock: 3
  roadWidth: 7
  groundHeight: 64
...
```
----------------

If problems arise, create an issue or write us on Discord.

| Discord |
| :---: |
[![Discord](https://img.shields.io/discord/639130989708181535.svg?style=flat-square&label=discord&colorB=7289da)](https://discord.gg/5tYC5dJ) |
