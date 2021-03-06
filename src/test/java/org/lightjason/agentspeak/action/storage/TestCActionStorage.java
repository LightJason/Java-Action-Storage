/*
 * @cond LICENSE
 * ######################################################################################
 * # LGPL License                                                                       #
 * #                                                                                    #
 * # This file is part of the LightJason                                                #
 * # Copyright (c) 2015-19, LightJason (info@lightjason.org)                            #
 * # This program is free software: you can redistribute it and/or modify               #
 * # it under the terms of the GNU Lesser General Public License as                     #
 * # published by the Free Software Foundation, either version 3 of the                 #
 * # License, or (at your option) any later version.                                    #
 * #                                                                                    #
 * # This program is distributed in the hope that it will be useful,                    #
 * # but WITHOUT ANY WARRANTY; without even the implied warranty of                     #
 * # MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the                      #
 * # GNU Lesser General Public License for more details.                                #
 * #                                                                                    #
 * # You should have received a copy of the GNU Lesser General Public License           #
 * # along with this program. If not, see http://www.gnu.org/licenses/                  #
 * ######################################################################################
 * @endcond
 */

package org.lightjason.agentspeak.action.storage;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lightjason.agentspeak.language.CRawTerm;
import org.lightjason.agentspeak.language.ITerm;
import org.lightjason.agentspeak.language.execution.CContext;
import org.lightjason.agentspeak.language.execution.IContext;
import org.lightjason.agentspeak.language.execution.instantiable.plan.IPlan;
import org.lightjason.agentspeak.testing.IBaseTest;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.logging.LogManager;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;


/**
 * test action storage
 */
public final class TestCActionStorage extends IBaseTest
{
    /**
     * execute context
     */
    private IContext m_context;

    static
    {
        LogManager.getLogManager().reset();
    }

    /**
     * initialize
     *
     * @throws Exception is thrown on any error
     */
    @BeforeEach
    public void initialize() throws Exception
    {
        m_context = new CContext(
            new CAgentGenerator().generatesingle(),
            IPlan.EMPTY,
            Collections.emptyList()
        );
    }

    /**
     * test add action without forbidden keys
     */
    @Test
    public void addwithoutkeys()
    {
        Assumptions.assumeTrue( Objects.nonNull( m_context ) );

        new CAdd().execute(
            false, m_context,
            Stream.of(  "testnumber", 123, "teststring", "foobar" ).map( CRawTerm::of ).collect( Collectors.toList() ),
            Collections.emptyList()
        );

        Assertions.assertEquals( 2, m_context.agent().storage().size() );
        Assertions.assertEquals( 123, m_context.agent().storage().get( "testnumber" ) );
        Assertions.assertEquals( "foobar", m_context.agent().storage().get( "teststring" ) );
    }

    /**
     * test add action with forbidden keys
     */
    @Test
    public void addwithkeys()
    {
        new CAdd( "bar" ).execute(
            false, m_context,
            Stream.of( "bar", 123 ).map( CRawTerm::of ).collect( Collectors.toList() ),
            Collections.emptyList()
        );

        Assertions.assertEquals( 0, m_context.agent().storage().size() );
        Assertions.assertNull( m_context.agent().storage().get( "bar" ) );
    }


    /**
     * test add action with forbidden key strean
     */
    @Test
    public void addwithkeystrean()
    {
        new CAdd( Stream.of( "abc" ) ).execute(
            false, m_context,
            Stream.of( "abc", 123 ).map( CRawTerm::of ).collect( Collectors.toList() ),
            Collections.emptyList()
        );

        Assertions.assertEquals( 0, m_context.agent().storage().size() );
        Assertions.assertNull( m_context.agent().storage().get( "abc" ) );
    }


    /**
     * test remove action without keys
     */
    @Test
    public void removewithoutkeys()
    {
        Assumptions.assumeTrue( Objects.nonNull( m_context ) );

        m_context.agent().storage().put( "xxx", 123 );

        final List<ITerm> l_return = new ArrayList<>();
        new CRemove().execute(
            false, m_context,
            Stream.of( "xxx" ).map( CRawTerm::of ).collect( Collectors.toList() ),
            l_return
        );

        Assertions.assertTrue( m_context.agent().storage().isEmpty() );
        Assertions.assertNull( m_context.agent().storage().get( "xxx" ) );
        Assertions.assertEquals( 1, l_return.size() );
        Assertions.assertEquals( Integer.valueOf( 123 ), l_return.get( 0 ).<Integer>raw() );
    }


    /**
     * test clear action without keys
     */
    @Test
    public void clearwithoutkeys()
    {
        Assumptions.assumeTrue( Objects.nonNull( m_context ) );

        IntStream.range( 0, 100 )
                 .mapToObj( i -> RandomStringUtils.random( 25 ) )
                 .forEach( i -> m_context.agent().storage().put( i, RandomStringUtils.random( 5 ) ) );

        Assertions.assertEquals( 100, m_context.agent().storage().size() );

        new CClear().execute(
            false, m_context,
            Collections.emptyList(),
            Collections.emptyList()
        );

        Assertions.assertTrue( m_context.agent().storage().isEmpty() );
    }


    /**
     * test remove action without keys
     */
    @Test
    public void removewithkeys()
    {
        Assumptions.assumeTrue( Objects.nonNull( m_context ) );

        m_context.agent().storage().put( "foo", 123 );
        m_context.agent().storage().put( "bar", 456 );

        final List<ITerm> l_return = new ArrayList<>();
        new CRemove( "foo" ).execute(
            false, m_context,
            Stream.of( "foo", "bar" ).map( CRawTerm::of ).collect( Collectors.toList() ),
            l_return
        );

        Assertions.assertEquals( 1, m_context.agent().storage().size() );
        Assertions.assertNotNull( m_context.agent().storage().get( "foo" ) );
        Assertions.assertEquals( 1, l_return.size() );
        Assertions.assertEquals( Integer.valueOf( 456 ), l_return.get( 0 ).<Integer>raw() );
    }


    /**
     * test remove action without key stream
     */
    @Test
    public void removewithkeystream()
    {
        Assumptions.assumeTrue( Objects.nonNull( m_context ) );

        m_context.agent().storage().put( "xx", 189 );
        m_context.agent().storage().put( "yy", 267 );

        final List<ITerm> l_return = new ArrayList<>();
        new CRemove( Stream.of( "xx" ) ).execute(
            false, m_context,
            Stream.of( "xx", "yy" ).map( CRawTerm::of ).collect( Collectors.toList() ),
            l_return
        );

        Assertions.assertEquals( 1, m_context.agent().storage().size() );
        Assertions.assertNotNull( m_context.agent().storage().get( "xx" ) );
        Assertions.assertEquals( 1, l_return.size() );
        Assertions.assertEquals( Integer.valueOf( 267 ), l_return.get( 0 ).<Integer>raw() );
    }


    /**
     * test clear action with keys
     */
    @Test
    public void clearwithkeys()
    {
        Assumptions.assumeTrue( Objects.nonNull( m_context ) );

        IntStream.range( 0, 100 )
                 .forEach( i -> m_context.agent().storage().put( MessageFormat.format( "value {0}", i ), i ) );

        Assertions.assertEquals( m_context.agent().storage().size(), 100 );

        new CClear( "value 1", "value 5", "value 73" ).execute(
            false, m_context,
            Collections.emptyList(),
            Collections.emptyList()
        );

        Assertions.assertEquals( 3, m_context.agent().storage().size() );
        Assertions.assertArrayEquals( Stream.of( "value 73", "value 5", "value 1" ).toArray(), m_context.agent().storage().keySet().toArray() );
        Assertions.assertArrayEquals( Stream.of(  73, 5, 1 ).toArray(), m_context.agent().storage().values().toArray() );
    }


    /**
     * test clear action with key stream
     */
    @Test
    public void clearwithkeystream()
    {
        Assumptions.assumeTrue( Objects.nonNull( m_context ) );

        IntStream.range( 0, 100 )
                 .forEach( i -> m_context.agent().storage().put( MessageFormat.format( "value {0}", i ), i ) );

        Assertions.assertEquals( 100, m_context.agent().storage().size() );

        new CClear( Stream.of( "value 7", "value 23", "value 91" ) ).execute(
            false, m_context,
            Collections.emptyList(),
            Collections.emptyList()
        );

        Assertions.assertEquals( 3, m_context.agent().storage().size() );
        Assertions.assertArrayEquals( Stream.of( "value 7", "value 23", "value 91" ).toArray(), m_context.agent().storage().keySet().toArray() );
        Assertions.assertArrayEquals( Stream.of(  7, 23, 91 ).toArray(), m_context.agent().storage().values().toArray() );
    }


    /**
     * test exists action without keys
     */
    @Test
    public void existswithoutkeys()
    {
        Assumptions.assumeTrue( Objects.nonNull( m_context ) );

        final List<ITerm> l_content = IntStream.range( 0, 100 )
                                               .mapToObj( i -> RandomStringUtils.random( 25 ) )
                                               .peek( i -> m_context.agent().storage().put( i, RandomStringUtils.random( 5 ) ) )
                                               .map( CRawTerm::of )
                                               .collect( Collectors.toList() );

        final List<ITerm> l_return = new ArrayList<>();
        new CExists().execute(
            false, m_context,
            l_content,
            l_return
        );

        Assertions.assertEquals( 100, l_return.size() );
        Assertions.assertTrue( l_return.stream().allMatch( ITerm::raw ) );
    }


    /**
     * test exists action with keys
     */
    @Test
    public void existswithkeys()
    {
        Assumptions.assumeTrue( Objects.nonNull( m_context ) );

        m_context.agent().storage().put( "value 9", 5 );
        m_context.agent().storage().put( "value 7", 5 );

        final List<ITerm> l_return = new ArrayList<>();
        new CExists( "value 9", "value 77", "57" ).execute(
            false, m_context,
            Stream.of( "value 9", "value 7", "value 23", "value 77", "57", "123" ).map( CRawTerm::of ).collect( Collectors.toList() ),
            l_return
        );

        Assertions.assertArrayEquals(
            Stream.of( false, true, false, false, false, false ).toArray(),
            l_return.stream().map( ITerm::raw ).toArray()
        );
    }


    /**
     * test exists action with key stream
     */
    @Test
    public void existswithkeystream()
    {
        Assumptions.assumeTrue( Objects.nonNull( m_context ) );

        m_context.agent().storage().put( "value 33", 5 );
        m_context.agent().storage().put( "value 177", 5 );
        m_context.agent().storage().put( "value 23", 19 );

        final List<ITerm> l_return = new ArrayList<>();
        new CExists( Stream.of( "value 33", "value 88", "23" ) ).execute(
            false, m_context,
            Stream.of( "value 33", "value 177", "value 23", "value 137" ).map( CRawTerm::of ).collect( Collectors.toList() ),
            l_return
        );

        Assertions.assertArrayEquals(
            Stream.of( false, true, true, false ).toArray(),
            l_return.stream().map( ITerm::raw ).toArray()
        );
    }



    /**
     * test for checking minimal arguments
     */
    @Test
    public void arguments()
    {
        Assertions.assertArrayEquals(

            Stream.of( 1, 0, 1, 1 ).toArray(),

            Stream.of(
                new CAdd().minimalArgumentNumber(),
                new CClear().minimalArgumentNumber(),
                new CExists().minimalArgumentNumber(),
                new CRemove().minimalArgumentNumber()
            ).toArray()

        );
    }



    /**
     * test resolver access
     */
    @Test
    public void resolver()
    {
        final Set<String> l_keys = Stream.of( "a", "x", "y" ).collect( Collectors.toSet() );

        Assertions.assertArrayEquals(
            Stream.of( true, false ).toArray(),
            new CAdd( l_keys::contains ).forbiddenkeys( "x", "z" ).toArray()
        );

        Assertions.assertArrayEquals(
            Stream.of( true, false ).toArray(),
            new CRemove( l_keys::contains ).forbiddenkeys( "x", "z" ).toArray()
        );

        Assertions.assertArrayEquals(
            Stream.of( true, false ).toArray(),
            new CClear( l_keys::contains ).forbiddenkeys( "x", "z" ).toArray()
        );

        Assertions.assertArrayEquals(
            Stream.of( true, false ).toArray(),
            new CExists( l_keys::contains ).forbiddenkeys( "x", "z" ).toArray()
        );
    }

}
