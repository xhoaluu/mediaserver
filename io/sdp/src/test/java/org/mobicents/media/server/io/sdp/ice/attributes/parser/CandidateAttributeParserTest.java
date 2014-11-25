package org.mobicents.media.server.io.sdp.ice.attributes.parser;

import junit.framework.Assert;

import org.junit.Test;
import org.mobicents.media.server.io.sdp.SdpException;
import org.mobicents.media.server.io.sdp.ice.attributes.CandidateAttribute;

/**
 * 
 * @author Henrique Rosa (henrique.rosa@telestax.com)
 *
 */
public class CandidateAttributeParserTest {
	
	private final CandidateAttributeParser parser = new CandidateAttributeParser();
	
	@Test
	public void testCanParse() {
		// given
		String validHost = "a=candidate:1995739850 1 udp 2113937151 192.168.1.65 54550 typ host generation 0";
		String invalidHost1 = "a=candidate:xyz 1 udp 2113937151 192.168.1.65 54550 typ host generation 0";
		String invalidHost2 = "a=candidate:1995739850 x udp 2113937151 192.168.1.65 54550 typ host generation 0";
		String invalidHost3 = "a=candidate:1995739850 1 udp xyz 192.168.1.65 54550 typ host generation 0";
		String validSrflx = "a=candidate:2162486046 1 udp 1845501695 85.241.121.60 60495 typ srflx raddr 192.168.1.65 rport 54550 generation 0";
		String invalidSrflx1 = "a=candidate:2162486046 1 udp 1845501695 85.241.121.60 60495 typ srflx rport 54550 generation 0";
		String invalidSrflx2 = "a=candidate:2162486046 1 udp 1845501695 85.241.121.60 60495 typ srflx raddr 192.168.1.65 generation 0";
		String invalidSrflx3 = "a=candidate:2162486046 1 udp 1845501695 85.241.121.60 60495 typ srflx raddr 192.168.1.65 rport 54550";
		String validRelay = "a=candidate:2564697628 1 udp 33562367 75.126.93.124 53056 typ relay raddr 85.241.121.60 rport 55027 generation 0";
		
		// when/then
		Assert.assertTrue(parser.canParse(validHost));
		Assert.assertFalse(parser.canParse(invalidHost1));
		Assert.assertFalse(parser.canParse(invalidHost2));
		Assert.assertFalse(parser.canParse(invalidHost3));
		Assert.assertTrue(parser.canParse(validSrflx));
		Assert.assertFalse(parser.canParse(invalidSrflx1));
		Assert.assertFalse(parser.canParse(invalidSrflx2));
		Assert.assertFalse(parser.canParse(invalidSrflx3));
		Assert.assertTrue(parser.canParse(validRelay));
	}
	
	@Test
	public void testParseHostCandidate() throws SdpException {
		// given
		String line = "a=candidate:1995739850 1 udp 2113937151 192.168.1.65 54550 typ host generation 0";
		
		// when
		CandidateAttribute candidate = parser.parse(line);
		
		// then
		Assert.assertEquals(1995739850L, candidate.getFoundation());
		Assert.assertEquals(1, candidate.getComponentId());
		Assert.assertEquals("udp", candidate.getProtocol());
		Assert.assertEquals(2113937151, candidate.getPriority());
		Assert.assertEquals("192.168.1.65", candidate.getAddress());
		Assert.assertEquals(54550, candidate.getPort());
		Assert.assertEquals("host", candidate.getCandidateType());
		Assert.assertEquals(0, candidate.getGeneration());
		Assert.assertNull(candidate.getRelatedAddress());
		Assert.assertEquals(0, candidate.getRelatedPort());
	}

	@Test
	public void testParseSrflxCandidate() throws SdpException {
		// given
		String line = "a=candidate:2162486046 1 udp 1845501695 85.241.121.60 60495 typ srflx raddr 192.168.1.65 rport 54550 generation 0";
		
		// when
		CandidateAttribute candidate = parser.parse(line);
		
		// then
		Assert.assertEquals(2162486046L, candidate.getFoundation());
		Assert.assertEquals(1, candidate.getComponentId());
		Assert.assertEquals("udp", candidate.getProtocol());
		Assert.assertEquals(1845501695, candidate.getPriority());
		Assert.assertEquals("85.241.121.60", candidate.getAddress());
		Assert.assertEquals(60495, candidate.getPort());
		Assert.assertEquals("srflx", candidate.getCandidateType());
		Assert.assertEquals("192.168.1.65", candidate.getRelatedAddress());
		Assert.assertEquals(54550, candidate.getRelatedPort());
		Assert.assertEquals(0, candidate.getGeneration());
	}
	
	@Test
	public void testParseOverwrite() throws SdpException {
		// given
		String line1 = "a=candidate:2162486046 1 udp 1845501695 85.241.121.60 60495 typ srflx raddr 192.168.1.65 rport 54550 generation 0";
		String line2 = "a=candidate:1995739850 1 udp 2113937151 192.168.1.65 54550 typ host generation 0";
		
		// when
		CandidateAttribute candidate = parser.parse(line1);
		parser.parse(candidate, line2);
		
		// then
		Assert.assertEquals(1995739850L, candidate.getFoundation());
		Assert.assertEquals(1, candidate.getComponentId());
		Assert.assertEquals("udp", candidate.getProtocol());
		Assert.assertEquals(2113937151, candidate.getPriority());
		Assert.assertEquals("192.168.1.65", candidate.getAddress());
		Assert.assertEquals(54550, candidate.getPort());
		Assert.assertEquals("host", candidate.getCandidateType());
		Assert.assertEquals(0, candidate.getGeneration());
		Assert.assertNull(candidate.getRelatedAddress());
		Assert.assertEquals(0, candidate.getRelatedPort());
	}

}
