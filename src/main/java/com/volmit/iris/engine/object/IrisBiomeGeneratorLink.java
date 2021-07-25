/*
 * Iris is a World Generator for Minecraft Bukkit Servers
 * Copyright (c) 2021 Arcane Arts (Volmit Software)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.volmit.iris.engine.object;

import com.volmit.iris.engine.cache.AtomicCache;
import com.volmit.iris.engine.data.DataProvider;
import com.volmit.iris.engine.interpolation.IrisInterpolation;
import com.volmit.iris.engine.object.annotations.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@Desc("This represents a link to a generator for a biome")
@Data
public class IrisBiomeGeneratorLink {

    @RegistryListGenerator
    @Desc("The generator id")
    private String generator = "default";

    @DependsOn({"min", "max"})
    @Required
    @MinNumber(-256) // TODO: WARNING HEIGHT
    @MaxNumber(256) // TODO: WARNING HEIGHT
    @Desc("The min block value (value + fluidHeight)")
    private int min = 0;

    @DependsOn({"min", "max"})
    @Required
    @MinNumber(-256) // TODO: WARNING HEIGHT
    @MaxNumber(256) // TODO: WARNING HEIGHT
    @Desc("The max block value (value + fluidHeight)")
    private int max = 0;

    private final transient AtomicCache<IrisGenerator> gen = new AtomicCache<>();

    public IrisGenerator getCachedGenerator(DataProvider g) {
        return gen.aquire(() ->
        {
            IrisGenerator gen = g.getData().getGeneratorLoader().load(getGenerator());

            if (gen == null) {
                gen = new IrisGenerator();
            }

            return gen;
        });
    }

    public double getHeight(DataProvider xg, double x, double z, long seed) {
        double g = getCachedGenerator(xg).getHeight(x, z, seed);
        g = g < 0 ? 0 : g;
        g = g > 1 ? 1 : g;

        return IrisInterpolation.lerp(min, max, g);
    }
}
