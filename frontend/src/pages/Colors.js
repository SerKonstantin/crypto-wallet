import React from 'react';
import styled from 'styled-components';
import { Container } from '../styles/CommonStyles';
import Button from '../components/Button'; // Assuming Button is already styled and imported

const ColorSwatch = styled.div`
  padding: 20px;
  border-radius: 8px;
  text-align: center;
  color: ${({ color }) => color.text};
  background-color: ${({ color }) => color.background};
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 15px;
`;

const Title = styled.h4`
  margin: 0;
  font-size: 1rem;
`;

const Description = styled.p`
  margin: 5px 0 0;
  font-size: 0.8rem;
  opacity: 0.75;
`;

const BorderedText = styled.div`
  border: 2px solid ${({ theme }) => theme.borderColor};
  padding: 10px;
  margin-top: 10px;
  border-radius: 4px;
  color: ${({ theme }) => theme.text};
`;

function Colors() {
  return (
    <Container>
      <h1>Theme Color Preview</h1>

      {/* Body background section */}
      <ColorSwatch
        color={{
          background: ({ theme }) => theme.body,
          text: ({ theme }) => theme.text,
        }}
      >
        <Title>Body / Text</Title>
        <Description>Background: body / Text: text</Description>
      </ColorSwatch>
      <ColorSwatch
        color={{
          background: ({ theme }) => theme.body,
          text: ({ theme }) => theme.labelColor,
        }}
      >
        <Title>Body / Label Color</Title>
        <Description>Background: body / Text: labelColor</Description>
      </ColorSwatch>
      <ColorSwatch
        color={{
          background: ({ theme }) => theme.body,
          text: ({ theme }) => theme.infoText,
        }}
      >
        <Title>Body / Info Text</Title>
        <Description>Background: body / Text: infoText</Description>
      </ColorSwatch>

      <ColorSwatch
        color={{
          background: ({ theme }) => theme.body,
          text: ({ theme }) => theme.text,
        }}
      >
        <Title>Text with Border on Body</Title>
        <BorderedText>Example Text with Border</BorderedText>
      </ColorSwatch>

      <ColorSwatch
        color={{
          background: ({ theme }) => theme.body,
          text: ({ theme }) => theme.text,
        }}
      >
        <Title>Button on Body Background</Title>
        <Button>Example Button</Button>
      </ColorSwatch>

      {/* Secondary background section */}
      <ColorSwatch
        color={{
          background: ({ theme }) => theme.secondaryBackground,
          text: ({ theme }) => theme.text,
        }}
      >
        <Title>Secondary BG / Text</Title>
        <Description>Background: secondaryBackground / Text: text</Description>
      </ColorSwatch>
      <ColorSwatch
        color={{
          background: ({ theme }) => theme.secondaryBackground,
          text: ({ theme }) => theme.labelColor,
        }}
      >
        <Title>Secondary BG / Label Color</Title>
        <Description>
          Background: secondaryBackground / Text: labelColor
        </Description>
      </ColorSwatch>
      <ColorSwatch
        color={{
          background: ({ theme }) => theme.secondaryBackground,
          text: ({ theme }) => theme.infoText,
        }}
      >
        <Title>Secondary BG / Info Text</Title>
        <Description>
          Background: secondaryBackground / Text: infoText
        </Description>
      </ColorSwatch>

      <ColorSwatch
        color={{
          background: ({ theme }) => theme.secondaryBackground,
          text: ({ theme }) => theme.text,
        }}
      >
        <Title>Text with Border on Secondary Background</Title>
        <BorderedText>Example Text with Border</BorderedText>
      </ColorSwatch>

      <ColorSwatch
        color={{
          background: ({ theme }) => theme.secondaryBackground,
          text: ({ theme }) => theme.text,
        }}
      >
        <Title>Button on Secondary Background</Title>
        <Button>Example Button</Button>
      </ColorSwatch>

      {/* Info background section */}
      <ColorSwatch
        color={{
          background: ({ theme }) => theme.infoBg,
          text: ({ theme }) => theme.text,
        }}
      >
        <Title>Info BG / Text</Title>
        <Description>Background: infoBg / Text: text</Description>
      </ColorSwatch>
      <ColorSwatch
        color={{
          background: ({ theme }) => theme.infoBg,
          text: ({ theme }) => theme.labelColor,
        }}
      >
        <Title>Info BG / Label Color</Title>
        <Description>Background: infoBg / Text: labelColor</Description>
      </ColorSwatch>
      <ColorSwatch
        color={{
          background: ({ theme }) => theme.infoBg,
          text: ({ theme }) => theme.infoText,
        }}
      >
        <Title>Info BG / Info Text</Title>
        <Description>Background: infoBg / Text: infoText</Description>
      </ColorSwatch>

      <ColorSwatch
        color={{
          background: ({ theme }) => theme.infoBg,
          text: ({ theme }) => theme.text,
        }}
      >
        <Title>Text with Border on Info Background</Title>
        <BorderedText>Example Text with Border</BorderedText>
      </ColorSwatch>

      <ColorSwatch
        color={{
          background: ({ theme }) => theme.infoBg,
          text: ({ theme }) => theme.text,
        }}
      >
        <Title>Button on Info Background</Title>
        <Button>Example Button</Button>
      </ColorSwatch>
    </Container>
  );
}

export default Colors;
