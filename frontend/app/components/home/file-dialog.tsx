import React, { useRef } from 'react';
import { Button, ButtonColor } from './button';
import { IconType } from 'react-icons';

interface FileDialogProps {
    logo?: IconType;
    text?: string;
    validateFile: (file: File) => void;
}

const FileDialog: React.FC<FileDialogProps> = ({
    logo,
    text,
    validateFile,
}) => {
    const ref = useRef<HTMLInputElement>(null);

    const handleClick = (e?: React.MouseEvent<HTMLButtonElement>) => {
        if (ref.current) {
            ref.current.click();
        }
    };

    const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        if (e.target.files && e.target.files[0]) {
            validateFile(e.target.files[0]);
        }
    };

    return (
        <>
            <Button
                onClick={handleClick}
                text={text || ''}
                color={ButtonColor.primary}
                logo={logo}
                children={
                    <input
                        ref={ref}
                        type="file"
                        style={{ display: 'none' }}
                        onChange={handleFileChange}
                    />
                }
            />
        </>
    );
};

export default FileDialog;
